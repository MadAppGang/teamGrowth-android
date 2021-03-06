package com.madappgang.teamgrowth.presenters.updateProgress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.madappgang.identifolib.extensions.onError
import com.madappgang.identifolib.extensions.onSuccess
import com.madappgang.teamgrowth.data.TeamGrowthRepository
import com.madappgang.teamgrowth.domain.Progress
import com.madappgang.teamgrowth.domain.ProgressUpdate
import com.madappgang.teamgrowth.domain.UserGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/*
 * Created by Eugene Prytula on 4/13/21.
 * Copyright (c) 2021 MadAppGang. All rights reserved.
 */

@HiltViewModel
class UpdateProgressViewModel @Inject constructor(
    private val teamGrowthRepository: TeamGrowthRepository
) : ViewModel() {

    private val _updateGoalState = MutableStateFlow<UpdateProgressViewStates>(UpdateProgressViewStates.IDLE)
    val updateGoalState : StateFlow<UpdateProgressViewStates> = _updateGoalState.asStateFlow()

    fun updateGoal(progress : Int, goal: UserGoal) {
        viewModelScope.launch {
            _updateGoalState.emit(UpdateProgressViewStates.Loading)
            val progress = ProgressUpdate(
                goal.userId,
                goal.goalId,
                progress
            )
            teamGrowthRepository.updateProgress(progress).onSuccess { progress ->
                _updateGoalState.emit(UpdateProgressViewStates.GoalHasBeenUpdated(progress))
            }.onError {
                _updateGoalState.emit(UpdateProgressViewStates.Error)
            }
        }
    }

    fun resetState() {
        _updateGoalState.value = UpdateProgressViewStates.IDLE
    }
}