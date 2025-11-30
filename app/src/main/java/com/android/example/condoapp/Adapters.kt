package com.android.example.condoapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.example.condoapp.*
import com.google.android.material.chip.Chip

class AnnouncementAdapter(private val list: List<Announcement>) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val date: TextView = view.findViewById(R.id.tv_date)
        val content: TextView = view.findViewById(R.id.tv_content)
        val tag: TextView = view.findViewById(R.id.tv_tag)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.date.text = item.date
        holder.content.text = item.content
        holder.tag.text = item.type
    }
    override fun getItemCount() = list.size
}

class TransactionAdapter(private val list: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val concept: TextView = view.findViewById(R.id.tv_concept)
        val date: TextView = view.findViewById(R.id.tv_date)
        val amount: TextView = view.findViewById(R.id.tv_amount)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.concept.text = item.concept
        holder.date.text = item.date
        holder.amount.text = item.amount
        if (item.isPositive) holder.amount.setTextColor(holder.itemView.context.getColor(R.color.verde_esmeralda))
        else holder.amount.setTextColor(holder.itemView.context.getColor(R.color.black))
    }
    override fun getItemCount() = list.size
}

class ReservationAdapter(private val list: List<Reservation>) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val area: TextView = view.findViewById(R.id.tv_area)
        val date: TextView = view.findViewById(R.id.tv_res_date)
        val chip: Chip = view.findViewById(R.id.chip_status)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.area.text = item.areaName
        holder.date.text = item.dateTime
        holder.chip.text = item.status
    }
    override fun getItemCount() = list.size
}

class TicketAdapter(private val list: List<Ticket>) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_ticket_title)
        val status: TextView = view.findViewById(R.id.tv_ticket_status)
        val desc: TextView = view.findViewById(R.id.tv_ticket_desc)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.status.text = item.status
        holder.desc.text = item.description
    }
    override fun getItemCount() = list.size
}
// En Adapters.kt, agrega esta clase al final:

class ContactAdapter(
    private val list: List<Contact>,
    private val onCallClick: (String) -> Unit // Función lambda para manejar el click
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_contact_name)
        val desc: TextView = view.findViewById(R.id.tv_contact_desc)
        val btnCall: View = view.findViewById(R.id.btn_call_contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.desc.text = item.description

        // Al hacer clic en el botón de teléfono, ejecutamos la acción enviando el número
        holder.btnCall.setOnClickListener {
            onCallClick(item.phoneNumber)
        }
    }

    override fun getItemCount() = list.size
}