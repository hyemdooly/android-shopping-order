package woowacourse.shopping.view.productlist

import woowacourse.shopping.model.ProductModel

interface ProductListContract {
    interface View {
        fun showProducts(items: List<ProductListViewItem>)
        fun showCartCount(count: Int)
        fun showToastAddInCart()
        fun onClickProductDetail(product: ProductModel, lastViewedProduct: ProductModel?)
        fun notifyAddProducts(position: Int, size: Int)
        fun notifyRecentViewedChanged()
        fun notifyDataChanged(position: Int)
        fun stopLoading()
        fun showErrorMessageToast(message: String?)
    }

    interface Presenter {
        fun fetchProducts()
        fun showProductDetail(product: ProductModel)
        fun loadMoreProducts()
        fun updateRecentViewed(id: Int)
        fun insertCartProduct(id: Int)
        fun updateCartProductCount(cartId: Int, productId: Int, count: Int)
        fun fetchCartCount()
        fun fetchProductCount(id: Int)
        fun fetchProductsCounts()
    }
}
