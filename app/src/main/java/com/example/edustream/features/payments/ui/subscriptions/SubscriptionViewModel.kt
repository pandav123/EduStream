package com.example.edustream.features.payments.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.payments.data.local.dao.SubscriptionDao
import com.example.edustream.features.payments.data.local.entities.PlanType
import com.example.edustream.features.payments.data.local.entities.SubStatus
import com.example.edustream.features.payments.data.local.entities.SubscriptionEntity
import com.example.edustream.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) : ViewModel() {

    private val _subscription = MutableStateFlow<UiState<SubscriptionEntity?>>(UiState.Loading)
    val subscription = _subscription.asStateFlow()

    init {
        loadSubscription()
    }

    fun loadSubscription() {
        viewModelScope.launch {
            subscriptionDao.getSubscriptionByUserId("current_user").collect {
                _subscription.value = UiState.Success(it)
            }
        }
    }

    fun subscribe(planType: PlanType) {
        viewModelScope.launch {
            // Simulate Play Billing success
            val newSub = SubscriptionEntity(
                subscriptionId = "sub_${System.currentTimeMillis()}",
                userId = "current_user",
                planType = planType,
                status = SubStatus.ACTIVE,
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + if (planType == PlanType.MONTHLY) 30L * 24 * 60 * 60 * 1000 else 365L * 24 * 60 * 60 * 1000,
                autoRenew = true,
                playPurchaseToken = "token_${System.currentTimeMillis()}"
            )
            subscriptionDao.insertSubscription(newSub)
        }
    }
}
