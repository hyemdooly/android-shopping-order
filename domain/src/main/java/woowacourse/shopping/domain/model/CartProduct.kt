package woowacourse.shopping.domain.model

data class CartProduct(val id: Int, val count: Int, val product: Product) {
    companion object
}
