package woowacourse.shopping.presenter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.remote.result.DataResult
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentViewedRepository
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductWithCartInfo
import woowacourse.shopping.domain.model.ProductsWithCartItem
import woowacourse.shopping.model.toUiModel
import woowacourse.shopping.view.productdetail.ProductDetailContract
import woowacourse.shopping.view.productdetail.ProductDetailPresenter

class ProductDetailPresenterTest {
    private lateinit var view: ProductDetailContract.View
    private lateinit var presenter: ProductDetailContract.Presenter
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var recentViewedRepository: RecentViewedRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        productRepository = object : ProductRepository {
            override fun getProductsByRange(
                lastId: Int,
                pageItemCount: Int,
                callback: (DataResult<ProductsWithCartItem>) -> Unit,
            ) {
                callback(
                    DataResult.Success(
                        ProductsWithCartItem(
                            ProductListFixture.products.map {
                                ProductWithCartInfo(
                                    it,
                                    ProductWithCartInfo.CartItem(1, 1),
                                )
                            },
                            false,
                        ),
                    ),
                )
            }

            override fun getProductById(
                id: Int,
                callback: (DataResult<ProductWithCartInfo>) -> Unit,
            ) {
                callback(DataResult.Success(ProductWithCartInfo(ProductListFixture.products[0], null)))
            }
        }

        recentViewedRepository = object : RecentViewedRepository {
            override fun findAll(callback: (List<Product>) -> Unit) {
                callback(ProductListFixture.products)
            }

            override fun add(product: Product) {
            }

            override fun remove(id: Int) {
            }
        }

        cartRepository = object : CartRepository {
            override fun getAll(callback: (DataResult<List<CartProduct>>) -> Unit) {
                callback(DataResult.Success(CartProductsFixture.cartProducts))
            }

            override fun insert(
                productId: Int,
                quantity: Int,
                callback: (DataResult<Int>) -> Unit,
            ) {
                callback(DataResult.Success(1))
            }

            override fun update(
                cartId: Int,
                quantity: Int,
                callback: (DataResult<Boolean>) -> Unit,
            ) {
                callback(DataResult.Success(true))
            }

            override fun remove(cartId: Int, callback: (DataResult<Boolean>) -> Unit) {
                callback(DataResult.Success(true))
            }
        }

        presenter = ProductDetailPresenter(
            1,
            1,
            view,
            productRepository,
            cartRepository,
            recentViewedRepository,
        )
    }

    @Test
    fun 상품_상세_정보를_띄울_수_있다() {
        // given

        // when
        presenter.fetchProductDetail()

        // then
        verify(exactly = 1) { view.showProductDetail(any(), any()) }
    }

    @Test
    fun 장바구니에_상품을_추가할_수_있다() {
        // given

        // when
        presenter.putInCart(ProductListFixture.products[1].toUiModel(null, 2))

        // then
        verify(exactly = 1) { view.finishActivity(true) }
    }

    @Test
    fun 상품_개수를_추가할_수_있다() {
        // given

        // when
        presenter.plusCount()

        // then
        assertEquals(2, presenter.quantity.value)
    }

    @Test
    fun 상품_개수를_뺄_수_있다() {
        // given
        presenter.plusCount()
        presenter.plusCount()
        presenter.plusCount()

        // when
        presenter.minusCount()

        // then
        assertEquals(3, presenter.quantity.value)
    }
}