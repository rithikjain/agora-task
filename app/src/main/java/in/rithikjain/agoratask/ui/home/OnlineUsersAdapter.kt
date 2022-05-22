package `in`.rithikjain.agoratask.ui.home

import `in`.rithikjain.agoratask.databinding.OnlineUserItemBinding
import `in`.rithikjain.agoratask.models.User
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnlineUsersAdapter(private val onRingClick: (User) -> Unit) :
    RecyclerView.Adapter<OnlineUsersAdapter.ViewHolder>() {

    private var onlineUsers = mutableListOf<User>()

    fun updateOnlineUsers(users: List<User>) {
        onlineUsers = users as MutableList<User>
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: OnlineUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.usernameTextView.text = user.username
            if (user.inCall) {
                binding.statusTextView.text = "• In a call"
                binding.statusTextView.setTextColor(Color.parseColor("#FF0000"))
                binding.ringButton.visibility = View.GONE
            } else {
                binding.statusTextView.text = "• Available"
                binding.statusTextView.setTextColor(Color.parseColor("#006400"))
                binding.ringButton.visibility = View.VISIBLE
            }
            binding.ringButton.setOnClickListener {
                onRingClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            OnlineUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(onlineUsers[position])
    }

    override fun getItemCount() = onlineUsers.size
}