package de.lulebe.smartmirror.ui

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.google.gson.Gson
import de.lulebe.smartmirror.R
import de.lulebe.smartmirror.data.Module
import de.lulebe.smartmirror.data.RegistryModule
import us.feras.mdv.MarkdownView


class FieldLayout(context: Context, val module: Module, val registryModule: RegistryModule) : LinearLayout(context) {

    init {
        orientation = LinearLayout.VERTICAL
        registryModule.dataFields.forEachIndexed { index, dataField ->
            val child = LayoutInflater.from(context).inflate(R.layout.listitem_datafield, this, false)
            child.tag = index
            val et = child.findViewById(R.id.field_et) as EditText
            val sw = child.findViewById(R.id.field_sw) as Switch
            (child.findViewById(R.id.field_name) as TextView).text = dataField.name
            (child.findViewById(R.id.field_type) as TextView).text = dataField.type
            (child.findViewById(R.id.field_description) as MarkdownView).loadMarkdown(dataField.description)
            if (dataField.type == "bool") {
                sw.visibility = View.VISIBLE
                if (module.data.containsKey(dataField.name))
                    sw.isChecked = module.data[dataField.name] as Boolean? ?: false
            } else {
                et.visibility = View.VISIBLE
                if (module.data.containsKey(dataField.name))
                    et.setText(module.data[dataField.name] as String)
                when (dataField.type) {
                    "string" -> {
                        et.inputType = InputType.TYPE_CLASS_TEXT
                                .or(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                    }
                    "uint" -> {
                        et.inputType = InputType.TYPE_CLASS_NUMBER
                                .or(InputType.TYPE_NUMBER_FLAG_SIGNED)
                    }
                    "int" -> {
                        et.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                    "float" -> {
                        et.inputType = InputType.TYPE_CLASS_NUMBER
                                .or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    }
                    "ufloat" -> {
                        et.inputType = InputType.TYPE_CLASS_NUMBER
                                .or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                                .or(InputType.TYPE_NUMBER_FLAG_SIGNED)
                    }
                    "json" -> {
                        et.inputType = InputType.TYPE_CLASS_TEXT
                                .or(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                                .or(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                        et.setLines(8)
                    }
                }
            }
            addView(child)
        }
    }

    fun saveToModule () {
        for (i in 0..(childCount-1)) {
            val child = getChildAt(i)
            val field = registryModule.dataFields[i]
            if (field.type == "bool") {
                val value = (child.findViewById(R.id.field_sw) as Switch).isChecked
                module.data[field.name] = value
            } else {
                val value = (child.findViewById(R.id.field_et) as EditText).text.toString()
                when (field.type) {
                    "string" -> {
                        module.data[field.name] = value
                    }
                    "uint" -> {
                        module.data[field.name] = value.toInt()
                    }
                    "int" -> {
                        module.data[field.name] = value.toInt()
                    }
                    "float" -> {
                        module.data[field.name] = value.toDouble()
                    }
                    "ufloat" -> {
                        module.data[field.name] = value.toDouble()
                    }
                    "json" -> {
                        module.data[field.name] = Gson().fromJson(value, Any::class.java)
                    }
                }
            }
        }
    }

}