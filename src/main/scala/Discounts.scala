package adthena.task.discounts

import adthena.task.items.{Item, CountableItem}

trait Discount:
  val name: String
  def getName: String = name
  def calculate(items: Iterable[Item]): Option[Int]

case class ItemOnSale(name: String, item: Item, ratio: Float) extends Discount:
  def calculate(items: Iterable[Item]): Option[Int] =
    items.filter(_ == item).map(_.getPricePence).sum match
      case 0     => None
      case total => Some(Math.round(total * ratio))

case class BundledDiscountForSecondItem(
    name: String,
    itemToBuy: Item,
    quantityRequired: Int,
    itemOnDiscount: Item,
    maxQuantityDiscounted: Int,
    discountRatio: Float,
) extends Discount:
  def calculate(items: Iterable[Item]): Option[Int] =
    val itemsToBuyCount = items
      .filter(_ == itemToBuy)
      .map(_.getQuantity)
      .sum

    val itemOnDiscountCount = items
      .filter(_ == itemOnDiscount)
      .map(_.getQuantity)
      .sum

    val discountedQuantity =
      Math.min(
        itemsToBuyCount / quantityRequired * maxQuantityDiscounted,
        itemOnDiscountCount,
      )

    discountedQuantity match
      case 0 => None
      case quantity =>
        Some(
          Math.round(quantity * itemOnDiscount.getPricePence * discountRatio),
        )
