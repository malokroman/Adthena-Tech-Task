package adthena.task.catalogue

import adthena.task.items.Item
import adthena.task.discounts.Discount

trait Catalogue:
  def getItemFromName(name: String): Option[Item]
  def getActiveDiscounts: Iterable[Discount]

/* A good idea is to create a class that reads the
 * catalogue from data file or database.
 *
 * The catalogue is hard coded in its own file for this exercise. */
