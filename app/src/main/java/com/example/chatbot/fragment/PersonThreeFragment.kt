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
import com.example.chatbot.model.ResponseBody
import com.example.chatbot.databinding.FragmentThreeBinding
import com.example.chatbot.databinding.ItemMessageReceiveBinding
import com.example.chatbot.databinding.ItemMessageSendBinding
import com.example.chatbot.utility.StringConstants
import com.example.chatbot.viewmodel.ChatViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class PersonThreeFragment : Fragment(),ChatResponseListener {

    private lateinit var chatViewModel: ChatViewModel
    private var _binding: FragmentThreeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext:Context
    private val messageAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentThreeBinding.inflate(inflater, container, false)
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
            if(sendMessage.isNotEmpty()){
                val sendItemToBind = SendMessageItem(sendMessage)
                messageAdapter.add(sendItemToBind)
                binding.typeMessage.text.clear()
                //observeSendAndReceiveChat(sendMessage,"Amar")
                chatViewModel.initiateConnection(this,sendMessage,"Amar")
            }
        })
    }

    class SendMessageItem(private val sendMessage: String) : BindableItem<ItemMessageSendBinding>() {
        override fun getLayout(): Int {
            return R.layout.item_message_send
        }

        override fun bind(viewBinding: ItemMessageSendBinding, position: Int) {
            viewBinding.message = sendMessage
        }
    }

    class ReceiveMessageItem(private val receiveMessage: String) : BindableItem<ItemMessageReceiveBinding>() {
        override fun getLayout(): Int {
            return R.layout.item_message_receive
        }

        override fun bind(viewBinding: ItemMessageReceiveBinding, position: Int) {
            viewBinding.message = receiveMessage
        }
    }

    override fun chatResponseReceived(chat: ResponseBody) {
        scrollToLastPosition()
        if(chat.getMessageBody()!=null) {
            Log.d(
                "FragmentResponse", "onResponse with data = ${
                    chat.getMessageBody()?.getMessage().toString()
                }"
            )
            val responseMessage = chat.getMessageBody()?.getMessage().toString()
            if (responseMessage.isNotEmpty()) {
                val sendItemToBind = ReceiveMessageItem(responseMessage)
                messageAdapter.add(sendItemToBind)
            }
        }else{
            messageAdapter.add(ReceiveMessageItem(StringConstants.commonResponse))
        }
    }

    override fun chatResponseFailed(reason: String) {
        Log.d("FragmentResponse","chatResponseFailed = ${reason}")
        Toast.makeText(mContext,reason,Toast.LENGTH_LONG).show()
    }

    private fun scrollToLastPosition() {
        binding.recyclerView.postDelayed({
            binding.recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }, 100)
    }
}