package adthena.task.items

trait Item:
  val name: String
  def getName: String = name
  def getPricePence: Int
  def getQuantity: Int

case class CountableItem(
    name: String,
    unitPricePence: Int,
) extends Item:
  def getPricePence: Int =
    this.unitPricePence

  def getQuantity: Int = 1

/* Potential additional class:
 *  - WeightedItems
 *  - IndividuallyPricedItem
 * */
