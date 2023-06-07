package woowacourse.shopping.presenter

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrderSubmitDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.MypageRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.view.order.OrderContract
import woowacourse.shopping.view.order.OrderPresenter

class OrderPresenterTest {
    private lateinit var view: OrderContract.View
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var mypageRepository: MypageRepository
    private lateinit var orderRepository: OrderRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        mypageRepository = object : MypageRepository {
            override fun getCash(callback: (DataResult<Int>) -> Unit) {
                callback(DataResult.Success(40000))
            }

            override fun chargeCash(cash: Int, callback: (DataResult<Int>) -> Unit) {
            }
        }
        orderRepository = object : OrderRepository {
            override fun getAll(callback: (DataResult<List<Order>>) -> Unit) {
            }

            override fun getOrder(id: Int, callback: (DataResult<Order>) -> Unit) {
            }

            override fun order(
                cartProducts: OrderCartItemsDTO,
                callback: (DataResult<Int?>) -> Unit
            ) {
                callback(DataResult.Success(1))
            }
        }
        presenter = OrderPresenter(
            view,
            OrderCartProductModelFixture.orderCartProductsModel, orderRepository, mypageRepository
        )
    }

    @Test
    fun 주문_정보를_띄울_수_있다() {
        // given

        // when
        presenter.fetchOrder()

        // then
        verify(exactly = 1) { view.showOrder(any()) }
    }

    @Test
    fun 보유한_캐시가_총_가격_이상일_때_주문을_할_수_있다() {
        // given
        presenter.fetchOrder()

        // when
        presenter.order()

        // then
        verify(exactly = 1) { view.showOrderComplete(any()) }
    }

    @Test
    fun 보유한_캐시가_총_가격_미만일_때_주문을_할_수_없다() {
        // give
        mypageRepository = object : MypageRepository {
            override fun getCash(callback: (DataResult<Int>) -> Unit) {
                callback(DataResult.Success(10000))
            }

            override fun chargeCash(cash: Int, callback: (DataResult<Int>) -> Unit) {
            }
        }
        presenter = OrderPresenter(
            view,
            OrderCartProductModelFixture.orderCartProductsModel, orderRepository, mypageRepository
        )
        presenter.fetchOrder()

        // when
        presenter.order()

        // then
        verify(exactly = 1) { view.showUnableToast() }
    }
}