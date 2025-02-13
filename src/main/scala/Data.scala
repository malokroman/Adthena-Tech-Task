package adthena.task.data

import adthena.task.catalogue.Catalogue
import adthena.task.items.{Item, CountableItem}
import adthena.task.discounts.{
  Discount,
  ItemOnSale,
  BundledDiscountForSecondItem,
}

object ExampleCatalogue extends Catalogue:
  val nameItemMap = Map(
    "Soup" -> CountableItem("Soup", 65),
    "Bread" -> CountableItem("Bread", 80),
    "Milk" -> CountableItem("Milk", 130),
    "Apple" -> CountableItem("Apple", 100),
  )

  val activeDiscounts = List[Discount](
    ItemOnSale("Apple 10% off", getItemFromName("Apple").get, 0.1),
    BundledDiscountForSecondItem(
      "Loaf of Bread half price per two tins of soup",
      itemToBuy = getItemFromName("Soup").get,
      quantityRequired = 2,
      itemOnDiscount = getItemFromName("Bread").get,
      maxQuantityDiscounted = 1,
      discountRatio = 0.5f,
    ),
  )

  def getItemFromName(name: String): Option[Item] = nameItemMap.get(name)

  def getActiveDiscounts: Iterable[Discount] = activeDiscounts
