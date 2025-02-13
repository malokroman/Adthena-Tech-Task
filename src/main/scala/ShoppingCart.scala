package adthena.task.shoppingcart

import adthena.task.catalogue.Catalogue
import adthena.task.items.Item
import adthena.task.discounts.Discount
import adthena.task.currencies.penceToString

import scala.collection.mutable.ListBuffer

case class ShoppingCart(
    catalogue: Catalogue,
    items: ListBuffer[Item] = ListBuffer[Item](),
):
  def addItemFromString(itemName: String): Unit =
    items += catalogue
      .getItemFromName(itemName)
      .getOrElse(throw new RuntimeException(s"Item $itemName not found"))

  def calculateSubtotalPence: Int =
    items.map(_.getPricePence).sum

  def listAppliedDiscounts(): Iterable[(Discount, Int)] =
    catalogue.getActiveDiscounts
      .flatMap(discount => discount.calculate(items).map((discount, _)))

  /** Print the discount messages and return the total amount */
  def processDiscounts(): Int =
    val appliedDiscounts = listAppliedDiscounts()
    if appliedDiscounts.isEmpty then
      println("(No offers available)")
      0
    else
      var total = 0
      for (discount, amt) <- appliedDiscounts do
        println(s"${discount.getName}: ${penceToString(amt)}")
        total += amt
      total

  /** Print the details and return the total amount */
  def checkout(): Int =
    val subtotalPence = calculateSubtotalPence
    println(s"Subtotal: ${penceToString(subtotalPence)}")
    val discountPence = processDiscounts()
    val totalPence = subtotalPence - discountPence
    println(s"Total price: ${penceToString(totalPence)}")
    totalPence
