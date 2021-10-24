package com.example.chatbot.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatbot.ChatResponseListener
import com.example.chatbot.R
import com.example.chatbot.databinding.FragmentThreeBinding
import com.example.chatbot.databinding.ItemMessageReceiveBinding
import com.example.chatbot.databinding.ItemMessageSendBinding
import com.example.chatbot.databinding.ItemMessageSendOfflineBinding
import com.example.chatbot.model.ResponseBody
import com.example.chatbot.utility.HelperUtility
import com.example.chatbot.utility.PreferenceManager
import com.example.chatbot.utility.StringConstants
import com.example.chatbot.viewmodel.ChatViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.util.*


class PersonThreeFragment : Fragment(), ChatResponseListener {

    private val TAG = PersonThreeFragment::class.java.simpleName
    private lateinit var chatViewModel: ChatViewModel
    private var _binding: FragmentThreeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context
    private val messageAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var prefManager: PreferenceManager
    private var sentHashMap = HashMap<Int, String>()
    private var sentCountKey = 0
    private var receivedHashMap = HashMap<Int, String>()
    private var receivedCountKey = 0
    private var offlineHashMap = HashMap<Int, String>()
    private val gson = Gson()
    private var isAppDestroyed = false
    private lateinit var helperUtility: HelperUtility

    private val mConnReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val currentNetworkInfo =
                intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
            if (!isAppDestroyed && currentNetworkInfo!!.isConnected) {
                Log.d(TAG, "Connection Exists")
                if (offlineHashMap.size != 0) {
                    val adapterCount = messageAdapter.itemCount
                    Log.d(TAG, "Old Total Count = $adapterCount")
                    for (pos in adapterCount - 1 downTo 0) {
                        val group = messageAdapter.getGroup(pos)
                        Log.d(TAG, "Printing Group = $group")
                        if (group is SendOfflineMessageItem) {
                            Log.d(TAG, "Position Deleting = $pos")
                            messageAdapter.remove(group)
                            Log.d(TAG, "New Total Count = ${messageAdapter.itemCount}")
                        }
                    }
                    for ((key, value) in offlineHashMap) {
                        Log.d(TAG, "Old HashMap Size = ${offlineHashMap.size}")
                        sendMessageToChatServer(value)
                        Log.d(TAG, "New HashMap Size = ${offlineHashMap.size}")
                    }
                    offlineHashMap.clear()
                    prefManager.removeValueFromSharedPref(StringConstants.PERSON_THREE)
                }
            } else {
                Log.d(TAG, "Connection Does Not Exists")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isAppDestroyed = true
        Log.d(
            TAG,
            "FragmentDestroyed with SendHashMap size = ${sentHashMap.size} and ReceivedHashMap size = ${receivedHashMap.size}"+
                    "and OfflineHashMap size = ${offlineHashMap.size}"
        )
        sendToPrefManager(sentHashMap, receivedHashMap)
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefManager = PreferenceManager(mContext)
        helperUtility = HelperUtility(mContext)
        fetchFromPrefManager()
        activity?.registerReceiver(
            mConnReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentThreeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = messageAdapter
        scrollToLastPosition()
        binding.typeMessage.setOnClickListener {
            scrollToLastPosition()
        }
        binding.sendMessage.setOnClickListener(View.OnClickListener {
            scrollToLastPosition()
            val sendMessage = binding.typeMessage.text.toString()
            if (sendMessage.isNotEmpty()) {
                if (helperUtility.isNetworkAvailable()) {
                    sendMessageToChatServer(sendMessage)
                } else {
                    val sendItemToBind = SendOfflineMessageItem(sendMessage)
                    messageAdapter.add(sendItemToBind)
                    Snackbar.make(view,StringConstants.SNACKBAR_MESSAGE,Snackbar.LENGTH_SHORT)
                        .setActionTextColor(getResources().getColor(R.color.purple_200)).show()
                    Log.d(TAG, "ItemCount = ${messageAdapter.itemCount}")
                    updateOfflineSentHashMap(messageAdapter.itemCount, sendMessage)
                }
                binding.typeMessage.text.clear()
            }
        })
    }

    private fun sendMessageToChatServer(sendMessage: String) {
        val sendItemToBind = SendMessageItem(sendMessage)
        messageAdapter.add(sendItemToBind)
        chatViewModel.initiateConnection(this, sendMessage, StringConstants.PERSON_THREE)
        updateSentHashMap(sendMessage)
    }

    private fun updateSentHashMap(sendMessage: String) {
        sentHashMap[sentCountKey++] = sendMessage
    }

    private fun updateReceiveHashMap(receiveMessage: String) {
        receivedHashMap[receivedCountKey++] = receiveMessage
    }

    private fun updateOfflineSentHashMap(position: Int, sendMessage: String) {
        offlineHashMap[position] = sendMessage
        val offlineMessagesData = gson.toJson(offlineHashMap)
        prefManager.saveOfflineChatForLater(StringConstants.PERSON_THREE, offlineMessagesData)
    }

    private fun sendToPrefManager(
        sentMessagesMap: HashMap<Int, String>,
        receivedMessagesMap: HashMap<Int, String>
    ) {
        val sentMessages: String = gson.toJson(sentMessagesMap)
        val receivedMessages: String = gson.toJson(receivedMessagesMap)
        prefManager.saveChatForLater(StringConstants.PERSON_THREE, sentMessages, StringConstants.SENT)
        prefManager.saveChatForLater(
            StringConstants.PERSON_THREE,
            receivedMessages,
            StringConstants.RECEIVED
        )
    }

    private fun fetchFromPrefManager() {
        val type = object : TypeToken<HashMap<Int, String>>() {}.type
        val oldSentMessages = prefManager.getSentChatForLater(StringConstants.PERSON_THREE)
        val oldReceivedMessages = prefManager.getReceivedChatForLater(StringConstants.PERSON_THREE)
        val offlineSentMessages = prefManager.getOfflineChatForLater(StringConstants.PERSON_THREE)
        if (oldSentMessages.isNotEmpty()) {
            sentHashMap.putAll(gson.fromJson(oldSentMessages, type))
            sentCountKey = sentHashMap.size
        }
        if (oldReceivedMessages.isNotEmpty()) {
            receivedHashMap.putAll(gson.fromJson(oldReceivedMessages, type))
            receivedCountKey = receivedHashMap.size
        }
        if (offlineSentMessages.isNotEmpty()) {
            offlineHashMap.putAll(gson.fromJson(offlineSentMessages, type))
        }
        Log.d(
            TAG,
            "FragmentRestored with SendHashMap size = ${sentHashMap.size} and ReceivedHashMap size = ${receivedHashMap.size} " +
                    "and OfflineHashMap size = ${offlineHashMap.size}"
        )
        prepareInitialScreen()
    }

    private fun prepareInitialScreen() {
        if (sentHashMap.size == receivedHashMap.size) {
            Log.d(TAG, "Maps are equal")
            for ((key, value) in sentHashMap) {
                messageAdapter.add(SendMessageItem(sentHashMap.getValue(key)))
                messageAdapter.add(ReceiveMessageItem(receivedHashMap.getValue(key)))
            }
        } else if (sentHashMap.size > receivedHashMap.size) {
            Log.d(TAG, "SentMap is Bigger")
            for ((key, value) in sentHashMap) {
                messageAdapter.add(SendMessageItem(sentHashMap.getValue(key)))
                if (key <= receivedHashMap.size - 1) {
                    messageAdapter.add(ReceiveMessageItem(receivedHashMap.getValue(key)))
                }
            }
        }
        if (offlineHashMap.size != 0) {
            for ((key, value) in offlineHashMap) {
                messageAdapter.add(SendOfflineMessageItem(offlineHashMap.getValue(key)))
            }
        }
    }

    class SendMessageItem(private val sendMessage: String) :
        BindableItem<ItemMessageSendBinding>() {
        override fun getLayout(): Int {
            return R.layout.item_message_send
        }

        override fun bind(viewBinding: ItemMessageSendBinding, position: Int) {
            viewBinding.message = sendMessage
        }
    }

    class SendOfflineMessageItem(private val sendMessage: String) :
        BindableItem<ItemMessageSendOfflineBinding>() {
        override fun getLayout(): Int {
            return R.layout.item_message_send_offline
        }

        override fun bind(viewBinding: ItemMessageSendOfflineBinding, position: Int) {
            viewBinding.message = sendMessage
        }
    }

    class ReceiveMessageItem(private val receiveMessage: String) :
        BindableItem<ItemMessageReceiveBinding>() {
        override fun getLayout(): Int {
            return R.layout.item_message_receive
        }

        override fun bind(viewBinding: ItemMessageReceiveBinding, position: Int) {
            viewBinding.message = receiveMessage
        }
    }

    override fun chatResponseReceived(chat: ResponseBody) {
        scrollToLastPosition()
        if (chat.getMessageBody() != null) {
            Log.d(
                TAG, "onResponse with data = ${
                    chat.getMessageBody()?.getMessage().toString()
                }"
            )
            val responseMessage = chat.getMessageBody()?.getMessage().toString()
            if (responseMessage.isNotEmpty()) {
                val sendItemToBind = ReceiveMessageItem(responseMessage)
                messageAdapter.add(sendItemToBind)
                updateReceiveHashMap(responseMessage)
            }
        } else {
            messageAdapter.add(ReceiveMessageItem(StringConstants.COMMON_RESPONSE))
        }
    }

    override fun chatResponseFailed(reason: String) {
        Log.d(TAG, "chatResponseFailed = ${reason}")
        Toast.makeText(mContext, reason, Toast.LENGTH_LONG).show()
    }

    private fun scrollToLastPosition() {
        if (!isAppDestroyed) {
            binding.recyclerView.postDelayed({
                binding.recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            }, 100)
        }
    }
}