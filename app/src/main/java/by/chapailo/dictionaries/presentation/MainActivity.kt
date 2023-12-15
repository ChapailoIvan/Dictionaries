package by.chapailo.dictionaries.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import by.chapailo.dictionaries.R
import by.chapailo.dictionaries.data.DatabaseManagerImpl
import by.chapailo.dictionaries.databinding.ActivityMainBinding
import by.chapailo.dictionaries.domain.DatabaseData
import by.chapailo.dictionaries.domain.onSuccess
import by.chapailo.dictionaries.presentation.tables.TablesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Router {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(binding.fragmentContainer.id, TablesFragment())
                .commit()
        }
    }

    override fun navigateTo(dialogFragment: DialogFragment) {
        dialogFragment.show(supportFragmentManager, dialogFragment::class.java.simpleName)
    }

    override fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(fragment::class.simpleName)
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    override fun navigateBack() {
        supportFragmentManager.popBackStack()
    }
}