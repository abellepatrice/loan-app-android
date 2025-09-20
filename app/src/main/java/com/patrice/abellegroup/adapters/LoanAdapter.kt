package com.patrice.abellegroup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.patrice.abellegroup.R
import com.patrice.abellegroup.models.Loan

class LoanAdapter(private val loans: List<Loan>) :
    RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    class LoanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvLoanAmount)
        val tvInterest: TextView = view.findViewById(R.id.tvInterestRate)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvPurpose: TextView = view.findViewById(R.id.tvPurpose)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = loans[position]
        holder.tvAmount.text = "Amount: KES ${loan.amount}"
        holder.tvInterest.text = "Interest: ${loan.interestRate}%"
        holder.tvDuration.text = "Duration: ${loan.durationMonths} months"
        holder.tvPurpose.text = "Purpose: ${loan.purpose}"
        holder.tvStatus.text = "Status: ${loan.status}"
    }

    override fun getItemCount() = loans.size
}

//class LoanAdapter(private val loans: List<Loan>) :
//    RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {
//
//    class LoanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val tvAmount: TextView = view.findViewById(R.id.etAmount)
//        val tvPurpose: TextView = view.findViewById(R.id.etPurpose)
//        val tvDuration: TextView = view.findViewById(R.id.etDuration)
//        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_loan, parent, false)
//        return LoanViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
//        val loan = loans[position]
//        holder.tvAmount.text = "Amount: KES ${loan.amount}"
//        holder.tvPurpose.text = "Purpose: ${loan.purpose}"
//        holder.tvDuration.text = "Duration: ${loan.durationMonths} months"
//        holder.tvStatus.text = "Status: ${loan.status}"
//    }
//
//    override fun getItemCount() = loans.size
//}
