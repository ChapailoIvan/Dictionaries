package by.chapailo.dictionaries.presentation.table

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.chapailo.dictionaries.R
import by.chapailo.dictionaries.databinding.FragmentTableBinding
import by.chapailo.dictionaries.domain.ColumnType
import by.chapailo.dictionaries.domain.OrderByClause
import by.chapailo.dictionaries.presentation.Router
import by.chapailo.dictionaries.presentation.collectWithLifecycle
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import org.w3c.dom.Text

@AndroidEntryPoint
class TableFragment : Fragment() {

    companion object {
        private const val TAG = "TableFragment"
        private const val IGNORE_TAG = "IGNORE"

        private const val TABLE_KEY = "TABLE"

        fun newInstance(table: String): TableFragment {
            return TableFragment().apply {
                arguments = Bundle().apply {
                    putString(TABLE_KEY, table)
                }
            }
        }
    }

    private val binding: FragmentTableBinding by lazy {
        FragmentTableBinding.inflate(layoutInflater)
    }

    private val viewModel: TableViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = with(binding) {

        binding.buttonEdit.setOnClickListener { startUpdateChain() }
        binding.buttonAdd.setOnClickListener { startInsertChain() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeTableViewState()
        observeEvents()
    }

    private fun observeTableViewState() = collectWithLifecycle(Dispatchers.Main) {
        viewModel.stateFlow.collectLatest { (columnTypes, values, orderBy, orderType) ->
            binding.tableLayout.removeAllViews()

            addTableHeaders(columnTypes, orderBy, orderType)
            values.forEachIndexed { rowIndex, rowData ->
                addTableRow(rowIndex, columnTypes, rowData)
            }
        }
    }

    private fun observeEvents() = collectWithLifecycle {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is TableFragmentEvent.ShowSnackbar ->
                    Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()

                is TableFragmentEvent.ShowErrorDialog ->
                    showErrorDialog(event.message)
            }
        }
    }

    private fun addTableHeaders(columnTypes: List<ColumnType>, orderBy: String, orderType: OrderByClause.OrderType) {
        val headerTableRow = TableRow(requireContext()).apply { tag = IGNORE_TAG }

        columnTypes.forEach { columnType ->
            val columnHeaderView = produceView(headerTableRow, columnType).apply {
                setOnClickListener { viewModel.select(orderByColumn = columnType.name) }
                if (columnType.name == orderBy) {
                    (this as? TextView)?.let { textView ->
                        val drawable = when(orderType) {
                            OrderByClause.OrderType.ASC -> ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_arrow_up
                            )

                            OrderByClause.OrderType.DESC -> ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_arrow_down
                            )
                        }
                        drawable?.setTint(textView.currentTextColor)
                        drawable?.setBounds(0 , 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

                        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                    }
                }
            }
            headerTableRow.addView(columnHeaderView)
        }

        binding.tableLayout.addView(headerTableRow)
    }

    private fun addTableRow(rowIndex: Int, columnTypes: List<ColumnType>, values: List<String>) {
        val tableRow = createTableRow(rowIndex)

        columnTypes.zip(values).forEach { (columnType, columnData) ->
            val tableColumnData = produceView(tableRow, columnType, columnData)
            tableRow.addView(tableColumnData)
        }

        binding.tableLayout.addView(tableRow)
    }

    private fun startInsertChain() {
        binding.buttonDone.visibility = View.VISIBLE
        binding.buttonEdit.visibility = View.GONE
        binding.buttonAdd.visibility = View.GONE

        val rowIndex = viewModel.stateFlow.value.values.size
        val columnTypes = viewModel.stateFlow.value.columnTypes
        val tableRow = createTableRow(rowIndex).apply {

        }

        columnTypes.forEach { columnType ->
            val tableColumnData = produceView(tableRow, columnType, "").apply {
                (this as? TextView)?.let { textView ->
                    textView.hint = columnType.name
                    textView.setHintTextColor(textView.hintTextColors.withAlpha(100))
                    textView.isEnabled = true
                }
            }
            tableRow.addView(tableColumnData)
        }

        binding.tableLayout.addView(tableRow)
        binding.buttonDone.setOnClickListener {
            binding.tableLayout.removeView(tableRow)
            stopInsertChain(tableRow.getRowData())
        }
    }

    private fun stopInsertChain(data: Map<String, String>) {
        binding.buttonDone.visibility = View.GONE
        binding.buttonEdit.visibility = View.VISIBLE
        binding.buttonAdd.visibility = View.VISIBLE

        (binding.tableLayout.children.last() as? TableRow)?.let { tableRow ->
            tableRow.children.forEach { view ->
                view.isEnabled = false; view.clearFocus()
            }
        }

        viewModel.insert(data)
        viewModel.select()
    }

    private fun startUpdateChain() {
        binding.buttonDone.visibility = View.VISIBLE
        binding.buttonEdit.visibility = View.GONE
        binding.buttonAdd.visibility = View.GONE

        binding.buttonDone.setOnClickListener { stopUpdateChain() }

        binding.tableLayout.children
            .filter { view -> view.tag != IGNORE_TAG }
            .mapNotNull { view -> (view as? TableRow) }
            .map { view -> view.children }
            .flatten()
            .forEach { view -> view.isEnabled = true }
    }

    private fun stopUpdateChain() {
        binding.buttonDone.visibility = View.GONE
        binding.buttonEdit.visibility = View.VISIBLE
        binding.buttonAdd.visibility = View.VISIBLE

        binding.tableLayout.children
            .filter { view -> view.tag != IGNORE_TAG }
            .mapNotNull { view -> (view as? TableRow) }
            .map { view -> view.children }
            .flatten()
            .forEach { view -> view.isEnabled = false; view.clearFocus() }

        binding.tableLayout.children
            .filter { view -> view.tag != IGNORE_TAG }
            .mapNotNull { view -> (view as? TableRow) }
            .map { tableRow -> tableRow.tag as Int to tableRow.getRowData() }
            .forEach { (rowIndex, rowData) -> viewModel.update(rowIndex, rowData) }

        viewModel.select()
    }

    private fun performDeleteChain(data: Map<String, String>) {
        viewModel.delete(data)
        viewModel.select()
    }

    private fun showErrorDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this.requireContext()).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }

        alertDialog.show()
    }

    private fun createTableRow(rowIndex: Int): TableRow {
        return TableRow(requireContext()).apply {
            tag = rowIndex

            setOnLongClickListener {
                performDeleteChain(this.getRowData())
                true
            }
        }
    }

}