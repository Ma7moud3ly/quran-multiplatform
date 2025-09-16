package com.ma7moud3ly.quran.data.impl

import androidx.compose.runtime.mutableStateOf
import com.ma7moud3ly.quran.data.repository.RecitationRepository
import com.ma7moud3ly.quran.model.Recitation
import com.ma7moud3ly.quran.model.RecitationState
import com.ma7moud3ly.quran.model.testRecitation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single
class RecitationRepositoryImpl : RecitationRepository {
    private val _recitationFlow = MutableStateFlow<Recitation?>(null)
    override val recitationFlow: Flow<Recitation?> = _recitationFlow.asStateFlow()

    override val recitationState = mutableStateOf(RecitationState())

    override fun getRecitation(): Recitation {
        return _recitationFlow.value?.copy() ?: testRecitation
    }

    override fun setRecitation(recitation: Recitation) {
        _recitationFlow.value = recitation
    }

    override fun setLocalDataSourceOnline() {
        _recitationFlow.value?.setLocalDatasource()

    }

    override fun setOnlineDataSource() {
        _recitationFlow.value?.setOnlineDatasource()
    }

    override fun isPlayInBackground(): Boolean {
        return getRecitation().playInBackground
    }
}