package com.example.chatbot.fragment

import android.content.Context
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
import com.example.chatbot.databinding.FragmentOneBinding
import com.example.chatbot.databinding.ItemMessageReceiveBinding
import com.example.chatbot.databinding.ItemMessageSendBinding
import com.example.chatbot.model.ResponseBody
import com.example.chatbot.utility.PreferenceManager
import com.example.chatbot.utility.StringConstants
import com.example.chatbot.viewmodel.ChatViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import java.util.*


class PersonOneFragment : Fragment(), ChatResponseListener {

    private val TAG = PersonOneFragment::class.java.simpleName
    private lateinit var chatViewModel: ChatViewModel
    private var _binding: FragmentOneBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context
    private val messageAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var prefManager: PreferenceManager
    private var sentHashMap = HashMap<Int, String>()
    private var sentCountKey = 0
    private var receivedHashMap = HashMap<Int, String>()
    private var receivedCountKey = 0
    private val gson = Gson()
    private var isAppDestroyed = false

    override fun onDestroyView() {
        super.onDestroyView()
        isAppDestroyed = true
        Log.d(
            TAG,
            "FragmentDestroyed with SendHashMap size = ${sentHashMap.size} and ReceivedHashMap size = ${receivedHashMap.size}"
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
        fetchFromPrefManager()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentOneBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = messageAdapter
        binding.typeMessage.setOnClickListener {
            scrollToLastPosition()
        }
        binding.sendMessage.setOnClickListener(View.OnClickListener {
            scrollToLastPosition()
            val sendMessage = binding.typeMessage.text.toString()
            if (sendMessage.isNotEmpty()) {
                val sendItemToBind = SendMessageItem(sendMessage)
                messageAdapter.add(sendItemToBind)
                binding.typeMessage.text.clear()
                chatViewModel.initiateConnection(this, sendMessage, StringConstants.PERSON_ONE)
                updateSentHashMap(sendMessage)
            }
        })
    }

    private fun updateSentHashMap(sendMessage: String) {
        sentHashMap[sentCountKey++] = sendMessage
    }

    private fun updateReceiveHashMap(receiveMessage: String) {
        receivedHashMap[receivedCountKey++] = receiveMessage
    }

    private fun sendToPrefManager(
        sentMessagesMap: HashMap<Int, String>,
        receivedMessagesMap: HashMap<Int, String>
    ) {
        val sentMessages: String = gson.toJson(sentMessagesMap)
        val receivedMessages: String = gson.toJson(receivedMessagesMap)
        prefManager.saveChatForLater(StringConstants.PERSON_ONE, sentMessages, StringConstants.SENT)
        prefManager.saveChatForLater(StringConstants.PERSON_ONE, receivedMessages, StringConstants.RECEIVED)
    }

    private fun fetchFromPrefManager() {
        val offlineSentMessages = prefManager.getSentChatForLater(StringConstants.PERSON_ONE)
        val offlineReceivedMessages = prefManager.getReceivedChatForLater(StringConstants.PERSON_ONE)
        if (offlineSentMessages.isNotEmpty() && offlineReceivedMessages.isNotEmpty()) {
            val type = object : TypeToken<HashMap<Int, String>>() {}.type
            sentHashMap.putAll(gson.fromJson(offlineSentMessages, type))
            sentCountKey = sentHashMap.size
            receivedHashMap.putAll(gson.fromJson(offlineReceivedMessages, type))
            receivedCountKey = receivedHashMap.size
            Log.d(
                TAG,
                "FragmentRestored with SendHashMap size = ${sentHashMap.size} and ReceivedHashMap size = ${receivedHashMap.size}"
            )
            prepareInitialScreen()
        }
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