package woowacourse.shopping.view.productlist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.model.ProductModel

class ProductListAdapter(
    private val items: MutableList<ProductListViewItem>,
    private val onItemClick: OnItemClick,
) : RecyclerView.Adapter<ProductViewHolder>() {
    interface OnItemClick {
        fun onProductClick(product: ProductModel)
        fun onShowMoreClick()
        fun onProductClickAddFirst(id: Int)
        fun onProductUpdateCount(cartId: Int, productId: Int, count: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder.of(parent, ProductListViewType.values()[viewType], onItemClick)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder.RecentViewedViewHolder -> {
                holder.bind(items[position] as ProductListViewItem.RecentViewedItem, onItemClick)
            }
            is ProductViewHolder.ProductItemViewHolder -> {
                holder.bind(items[position] as ProductListViewItem.ProductItem)
            }
            is ProductViewHolder.ShowMoreViewHolder -> {
                return
            }
        }
    }

    fun updateItems(newItems: List<ProductListViewItem>) {
        val diffCallback = ProductListDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}
