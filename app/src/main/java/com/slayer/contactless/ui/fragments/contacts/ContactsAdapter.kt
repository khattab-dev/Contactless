package com.slayer.contactless.ui.fragments.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.models.Contact
import com.slayer.contactless.databinding.ItemRvContactsBinding

class ContactsAdapter(
    private val onMethodBtnClicked: ((Contact) -> Unit)
) : ListAdapter<Contact, ContactsAdapter.ViewHolder>(ContactsDiffUtil()) {
    inner class ViewHolder(
        private val binding: ItemRvContactsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Contact) {
            binding.apply {
                tvName.text = item.name
                tvPhone.text = item.phone

                btnMethods.setOnClickListener {
                    onMethodBtnClicked(item)
                }
            }
        }
    }

    class ContactsDiffUtil : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        return ViewHolder(
            ItemRvContactsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactsAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}