package com.example.edustream.features.payments.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.edustream.features.payments.data.local.dao.OrderDao
import com.example.edustream.features.payments.data.local.entities.OrderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val orderDao: OrderDao
) : ViewModel() {

    val orders: Flow<PagingData<OrderEntity>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { orderDao.getAllOrders() }
    ).flow.cachedIn(viewModelScope)
}
