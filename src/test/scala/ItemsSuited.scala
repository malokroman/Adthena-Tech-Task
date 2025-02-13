import adthena.task.items.CountableItem

class ItemsSuite extends munit.FunSuite:
  test("CountableItem should preserve the name passed to it") {
    for name <- Array("Word", "Name_with_underscore", "Name With Space") do
      val item = CountableItem(name, 100)
      assertEquals(item.getName, name)
  }

  test("CountableItem should preserve the unitPricePence") {
    /* Non-negative values not tested as behaviour it is not expected */
    for pricePence <- Array(10, 150, 147145) do
      val item = CountableItem("Placeholder", pricePence)
      assertEquals(item.getPricePence, pricePence)
  }

  test("CountableItem should always have a quantity of 1") {
    for (name, pricePence) <- Array("1", "2", "3", "4").zip(1 to 4) do
      val item = CountableItem(name, pricePence)
      assertEquals(item.getQuantity, 1)
  }
