package by.chapailo.dictionaries.presentation.tables

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.chapailo.dictionaries.presentation.Router
import by.chapailo.dictionaries.databinding.FragmentTablesBinding
import by.chapailo.dictionaries.presentation.table.TableFragment
import by.chapailo.dictionaries.presentation.collectWithLifecycle
import by.chapailo.dictionaries.presentation.dialogs.showWelcomeDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TablesFragment: Fragment() {

    private val viewModel: TablesViewModel by viewModels()

    private val router: Router
        get() = this.requireActivity() as Router

    private val binding: FragmentTablesBinding by lazy {
        FragmentTablesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = with(binding) {

        observeState()
        observeEvents()

        binding.listViewTables.setOnItemClickListener { _, _, position, _ ->
            val tableFragment =
                TableFragment.newInstance(viewModel.stateFlow.value.tables[position])
            router.navigateTo(tableFragment)
        }

        return root
    }

    private fun observeState() = collectWithLifecycle {
        viewModel.stateFlow.collect { state ->
            updateListView(tables = state.tables)
        }
    }

    private fun observeEvents() = collectWithLifecycle {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                TablesFragmentEvent.ShowWelcomeDialog ->
                    showWelcomeDialog(onPositiveButtonClick = viewModel::loadTables)
            }
        }
    }

    private fun updateListView(tables: List<String>) {
        val adapter = ArrayAdapter(this.requireContext(),
            android.R.layout.simple_list_item_1, tables)

        binding.listViewTables.adapter = adapter
    }
}