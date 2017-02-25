package de.lulebe.smartmirror.ui

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import de.lulebe.smartmirror.R
import de.lulebe.smartmirror.data.RegistryModule
import org.jetbrains.anko.onCheckedChange
import us.feras.mdv.MarkdownView
import java.util.*


class DataFieldsAdapter(val fields: List<RegistryModule.DataField>, val data: HashMap<String, Any>) : RecyclerView.Adapter<DataFieldsAdapter.VH>() {


    override fun getItemCount() = fields.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val field = fields[position]
        holder.name.text = field.name.replace("_", " ")
        holder.type.text = field.type
        holder.description.loadMarkdown(field.description)
        if (field.type == "bool") {
            if (data.containsKey(field.name))
                holder.switch.isChecked = data[field.name] as Boolean? ?: false
            holder.switch.visibility = View.VISIBLE
            holder.switch.onCheckedChange { compoundButton, b ->
                data[field.name] = b
            }
        } else {
            holder.editText.visibility = View.VISIBLE
            if (data.containsKey(field.name))
                holder.editText.setText(data[field.name] as String?)
            holder.editText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (field.type == "string")
                        data[field.name] = s.toString()
                    else if (field.type == "json")
                        Log.d("EDITED", "JSON") //TODO implement
                    else
                        data[field.name] = s.toString().toDouble()
                }

            })
            when (field.type) {
                "string" -> {
                    holder.editText.inputType = InputType.TYPE_CLASS_TEXT
                            .or(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                }
                "uint" -> {
                    holder.editText.inputType = InputType.TYPE_CLASS_NUMBER
                            .or(InputType.TYPE_NUMBER_FLAG_SIGNED)
                }
                "int" -> {
                    holder.editText.inputType = InputType.TYPE_CLASS_NUMBER
                }
                "float" -> {
                    holder.editText.inputType = InputType.TYPE_CLASS_NUMBER
                            .or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                }
                "ufloat" -> {
                    holder.editText.inputType = InputType.TYPE_CLASS_NUMBER
                            .or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                            .or(InputType.TYPE_NUMBER_FLAG_SIGNED)
                }
                "json" -> {
                    holder.editText.inputType = InputType.TYPE_CLASS_TEXT
                            .or(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                            .or(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    holder.editText.setLines(8)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_datafield, parent, false) as ViewGroup
        return VH(view)
    }

    inner class VH(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById(R.id.field_name) as TextView
        val type = itemView.findViewById(R.id.field_type) as TextView
        val description = itemView.findViewById(R.id.field_description) as MarkdownView
        val editText = itemView.findViewById(R.id.field_et) as EditText
        val switch = itemView.findViewById(R.id.field_sw) as Switch
    }

}