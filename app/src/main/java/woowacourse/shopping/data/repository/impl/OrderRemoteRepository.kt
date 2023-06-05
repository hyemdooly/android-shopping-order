package woowacourse.shopping.data.repository.impl

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.remote.OrderApi
import woowacourse.shopping.data.remote.RetrofitGenerator
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrderDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.ServerStoreRespository
import woowacourse.shopping.domain.model.Order

class OrderRemoteRepository(serverRepository: ServerStoreRespository) : OrderRepository {

    private val retrofitService =
        RetrofitGenerator.create(serverRepository.getServerUrl(), OrderApi::class.java)

    override fun getAll(callback: (DataResult<List<Order>>) -> Unit) {
        retrofitService.requestOrders().enqueue(object : retrofit2.Callback<OrdersDTO> {
            override fun onResponse(
                call: Call<OrdersDTO>,
                response: Response<OrdersDTO>,
            ) {
                if (!response.isSuccessful) {
                    onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                    return
                }
                response.body()?.let {
                    callback(DataResult.Success(it.orders))
                }
            }

            override fun onFailure(call: Call<OrdersDTO>, t: Throwable) {
                callback(DataResult.Failure(t.message ?: ""))
            }
        })
    }

    override fun getOrder(id: Int, callback: (DataResult<Order>) -> Unit) {
        retrofitService.requestOrderDetail(id).enqueue(object : retrofit2.Callback<OrderDTO> {
            override fun onResponse(
                call: Call<OrderDTO>,
                response: Response<OrderDTO>,
            ) {
                if (!response.isSuccessful) {
                    onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                    return
                }
                response.body()?.let {
                    callback(DataResult.Success(it.toDomain()))
                }
            }

            override fun onFailure(call: Call<OrderDTO>, t: Throwable) {
                callback(DataResult.Failure(t.message ?: ""))
            }
        })
    }

    override fun order(cartProducts: OrderCartItemsDTO, callback: (DataResult<Int?>) -> Unit) {
        retrofitService.requestOrderCartItems(cartProducts).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    onFailure(call, Throwable(SERVER_ERROR_MESSAGE))
                    return
                }
                response.body()?.let {
                    val orderId = response.headers()["Location"]?.substringAfterLast("/")?.toInt()
                    callback(DataResult.Success(orderId))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback(DataResult.Failure(t.message ?: ""))
            }
        })
    }

    private fun OrderDTO.toDomain(): Order {
        return Order(orderId, orderedDateTime, products, totalPrice)
    }

    companion object {
        private const val SERVER_ERROR_MESSAGE = ""
    }
}