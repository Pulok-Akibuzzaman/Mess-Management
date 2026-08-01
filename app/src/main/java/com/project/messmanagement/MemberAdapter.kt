package com.project.messmanagement

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(private val members: List<Member>) :
    RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitials: TextView = view.findViewById(R.id.tvInitials)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvRoomPhone: TextView = view.findViewById(R.id.tvRoomPhone)
        val tvMeals: TextView = view.findViewById(R.id.tvMeals)
        val tvDue: TextView = view.findViewById(R.id.tvDue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membercard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]

        holder.tvInitials.text = member.initials
        holder.tvName.text = member.name
        holder.tvStatus.text = member.status
        holder.tvRoomPhone.text = "${member.room} · ${member.phone}"
        holder.tvMeals.text = member.meals.toString()
        holder.tvDue.text = member.due

        if (member.status == "Active") {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_active)
            holder.tvStatus.setTextColor(Color.parseColor("#1B6B3A"))
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_away)
            holder.tvStatus.setTextColor(Color.parseColor("#B85C00"))
        }
    }

    override fun getItemCount() = members.size
}
