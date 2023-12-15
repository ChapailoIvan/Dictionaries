package by.chapailo.dictionaries.presentation.table

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import by.chapailo.dictionaries.R
import by.chapailo.dictionaries.databinding.DialogFragmentTableBinding
import by.chapailo.dictionaries.domain.ColumnType
import by.chapailo.dictionaries.presentation.collectWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableDialogFragment : DialogFragment() {
    
    companion object {
        fun newInstance(
            table: String,
            isNullable: Boolean
        ): TableDialogFragment {
            val tableDialogFragment = TableDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("TABLE", table)
                    putBoolean("IS_NULLABLE", isNullable)
                }
            }
            
            return tableDialogFragment
        }
    }

    private val binding by lazy {
        DialogFragmentTableBinding.inflate(layoutInflater)
    }

    private val viewModel: TableDialogViewModel by viewModels()

    var onNullPickedListener: (() -> Unit)? = null
    var onDataPickedListener: ((Map<String, String>) -> Unit)? = null 

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = with(binding) {

        val isNullable: Boolean = checkNotNull(arguments?.getBoolean("IS_NULLABLE"))
        val tableName: String = checkNotNull(arguments?.getString("TABLE"))

        binding.textViewTitle.text = getString(R.string.dialog_fragment_title, tableName)

        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        if (!isNullable) {
            binding.buttonNull.visibility = View.GONE
        }

        binding.buttonNull.setOnClickListener {
            onNullPickedListener?.invoke()
            dismiss()
        }

        isCancelable = false

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeTableState()
    }

    private fun observeTableState() = collectWithLifecycle {
        viewModel.stateFlow.collect { (columnTypes, values) ->
            binding.tableLayout.removeAllViews()

            addTableHeaders(columnTypes)
            values.forEach { rowData ->
                addTableRow(columnTypes, rowData)
            }
        }
    }

    private fun addTableHeaders(columnTypes: List<ColumnType>) {
        val headerTableRow = TableRow(requireContext())

        columnTypes.forEach { columnType ->
            val columnHeaderView = produceView(headerTableRow, columnType)
            headerTableRow.addView(columnHeaderView)
        }

        binding.tableLayout.addView(headerTableRow)
    }

    private fun addTableRow(columnTypes: List<ColumnType>, values: List<String>) {
        val tableRow = TableRow(requireContext()).apply {
            setOnClickListener {
                onDataPickedListener?.invoke(getRowData())
                dismiss()
            }
        }

        columnTypes.zip(values).forEach { (columnType, columnData) ->
            val tableColumnData = produceView(tableRow, columnType, columnData).apply {
                isEnabled = true
                isFocusable = false
                setOnClickListener { tableRow.performClick() }
            }
            tableRow.addView(tableColumnData)
        }

        binding.tableLayout.addView(tableRow)
    }

}
