import adthena.task.discounts.{ItemOnSale, BundledDiscountForSecondItem}
import adthena.task.items.{Item, CountableItem}

import scala.collection.mutable.ListBuffer

class DiscountsSuite extends munit.FunSuite:
  test("ItemOnSale should preserve the name passed to it") {
    val apple = CountableItem("Apple", 100)
    for name <- Array("Word", "Name_with_underscore", "Name With Space") do
      val discount = ItemOnSale(name, apple, 0.1)
      assertEquals(discount.getName, name)
  }

  test(
    "ItemOnSale should calculate to None when discount item is not in items",
  ) {
    val name = "Apple 10% off"
    val apple = CountableItem("Apple", 100)
    val appleOnSale = ItemOnSale(name, apple, 0.1)
    val orange = CountableItem("Orange", 125)

    assertEquals(appleOnSale.calculate(List()), None)
    assertEquals(appleOnSale.calculate(List(orange, orange)), None)
  }

  test(
    "ItemOnSale should calculate the discount based on the number of discounted items passed to it",
  ) {
    val name = "Apple 10% off"
    val apple = CountableItem("Apple", 100)
    val appleOnSale = ItemOnSale(name, apple, 0.1)
    val orange = CountableItem("Orange", 125)

    val items = ListBuffer[Item]()
    // No apple
    assertEquals(appleOnSale.calculate(items), None)
    items += apple
    // One apple at 100p
    assertEquals(appleOnSale.calculate(items), Some(10))
    items += apple
    // Two apples at 200p
    assertEquals(appleOnSale.calculate(items), Some(20))
    items += orange
    // Two apples at 200p
    assertEquals(appleOnSale.calculate(items), Some(20))
  }

  test("ItemOnSale should calculate the discount based on the product price") {
    val name = "Apple 10% off"

    for (pricePence, expectedOneApple, expectedTwoApples) <- Array(
        (100, Some(10), Some(20)),
        (110, Some(11), Some(22)),
        (105, Some(11), Some(21)), // Rounded to closest pence
      )
    do
      val apple = CountableItem("Apple", pricePence)
      val appleOnSale = ItemOnSale(name, apple, 0.1)
      val oneApple = Array(apple)
      val twoApples = Array(apple, apple)

      assertEquals(appleOnSale.calculate(oneApple), expectedOneApple)
      assertEquals(appleOnSale.calculate(twoApples), expectedTwoApples)
  }

  test("ItemOnSale should calculate the discount based on the ratio") {
    val apple = CountableItem("Apple", 100)
    val oneApple = Array(apple)
    val twoApples = Array(apple, apple)

    for (ratio, expectedOneApple, expectedTwoApples) <- Array(
        (0.1f, Some(10), Some(20)),
        (0.2f, Some(20), Some(40)),
        (0.5f, Some(50), Some(100)),
      )
    do
      val appleOnSale = ItemOnSale(s"Apple $ratio off", apple, ratio)
      assertEquals(appleOnSale.calculate(oneApple), expectedOneApple)
      assertEquals(appleOnSale.calculate(twoApples), expectedTwoApples)
  }

  test("BundledDiscountForSecondItem should preserve the name passed to it") {
    val apple = CountableItem("Apple", 100)
    val orange = CountableItem("Orange", 170)
    for name <- Array("Word", "Name_with_underscore", "Name With Space") do
      val discount = BundledDiscountForSecondItem(
        name = name,
        itemToBuy = apple,
        quantityRequired = 2,
        itemOnDiscount = orange,
        maxQuantityDiscounted = 1,
        discountRatio = 0.5,
      )
      assertEquals(discount.getName, name)
  }

  test(
    "BundledDiscountForSecondItem should calculate to None if count of itemToBuy is smaller than quantityRequired",
  ) {
    val bread = CountableItem("Bread", 80)
    val soup = CountableItem("Soup", 65)
    val apple = CountableItem("Apple", 100)
    val discount = BundledDiscountForSecondItem(
      name = "Half-price bread per two tins of soup",
      itemToBuy = soup,
      quantityRequired = 2,
      itemOnDiscount = bread,
      maxQuantityDiscounted = 1,
      discountRatio = 0.5,
    )
    val items = ListBuffer(apple)
    assertEquals(discount.calculate(items), None)
    items += bread // With bread but not soup
    assertEquals(discount.calculate(items), None)
    items += soup // With soup but not enough
    assertEquals(discount.calculate(items), None)
  }

  test(
    "BundledDiscountForSecondItem should calculate to None if discounted item not in items",
  ) {
    val bread = CountableItem("Bread", 80)
    val soup = CountableItem("Soup", 65)
    val apple = CountableItem("Apple", 100)
    val discount = BundledDiscountForSecondItem(
      name = "Half-price bread per two tins of soup",
      itemToBuy = soup,
      quantityRequired = 2,
      itemOnDiscount = bread,
      maxQuantityDiscounted = 1,
      discountRatio = 0.5,
    )
    val items = ListBuffer(apple)
    assertEquals(discount.calculate(items), None)
    items += soup
    items += soup // Just enough soup to trigger discount
    assertEquals(discount.calculate(items), None)
    items += soup
    items += soup // Just enough soup to trigger discount twice
    assertEquals(discount.calculate(items), None)
  }

  test(
    "BundledDiscountForSecondItem should be throttled by quantity of itemToBuy",
  ) {
    val bread = CountableItem("Bread", 80)
    val soup = CountableItem("Soup", 65)
    val discountOneRequired = BundledDiscountForSecondItem(
      name = "Half-price bread per two tins of soup",
      itemToBuy = soup,
      quantityRequired = 1,
      itemOnDiscount = bread,
      maxQuantityDiscounted = 1,
      discountRatio = 0.5,
    )
    val discountTwoRequired = BundledDiscountForSecondItem(
      name = "Half-price bread per two tins of soup",
      itemToBuy = soup,
      quantityRequired = 2,
      itemOnDiscount = bread,
      maxQuantityDiscounted = 1,
      discountRatio = 0.5,
    )
    val items = ListBuffer.fill(6)(bread)
    assertEquals(discountOneRequired.calculate(items), None)
    assertEquals(discountTwoRequired.calculate(items), None)
    items += soup
    assertEquals(discountOneRequired.calculate(items), Some(40))
    assertEquals(discountTwoRequired.calculate(items), None)
    items += soup
    assertEquals(discountOneRequired.calculate(items), Some(80))
    assertEquals(discountTwoRequired.calculate(items), Some(40))
    items += soup
    assertEquals(discountOneRequired.calculate(items), Some(120))
    assertEquals(discountTwoRequired.calculate(items), Some(40))
    items += soup
    assertEquals(discountOneRequired.calculate(items), Some(160))
    assertEquals(discountTwoRequired.calculate(items), Some(80))
    items += soup
    assertEquals(discountOneRequired.calculate(items), Some(200))
    assertEquals(discountTwoRequired.calculate(items), Some(80))
    items += soup
    assertEquals(discountOneRequired.calculate(items), Some(240))
    assertEquals(discountTwoRequired.calculate(items), Some(120))
  }

  test(
    "BundledDiscountForSecondItem should be throttled by quantity of itemOnDiscount",
  ) {
    val bread = CountableItem("Bread", 80)
    val soup = CountableItem("Soup", 65)
    val discount = BundledDiscountForSecondItem(
      name = "Half-price bread per two tins of soup",
      itemToBuy = soup,
      quantityRequired = 2,
      itemOnDiscount = bread,
      maxQuantityDiscounted = 1,
      discountRatio = 0.5,
    )
    val items = ListBuffer.fill(6)(soup)
    assertEquals(discount.calculate(items), None)
    items += bread
    assertEquals(discount.calculate(items), Some(40))
    items += bread
    assertEquals(discount.calculate(items), Some(80))
    items += bread
    assertEquals(discount.calculate(items), Some(120))
  }

  test(
    "BundledDiscountForSecondItem should handle maxQuantityDiscounted > 1 correctly",
  ) {
    val bread = CountableItem("Bread", 80)
    val soup = CountableItem("Soup", 65)
    val discount = BundledDiscountForSecondItem(
      name = "Up to two loaf of half-price bread per two tins of soup",
      itemToBuy = soup,
      quantityRequired = 2,
      itemOnDiscount = bread,
      maxQuantityDiscounted = 2,
      discountRatio = 0.5,
    )
    val items = ListBuffer.fill(3)(soup)
    assertEquals(discount.calculate(items), None)
    items += bread
    assertEquals(discount.calculate(items), Some(40))
    items += bread
    assertEquals(discount.calculate(items), Some(80))
    items += bread
    assertEquals(discount.calculate(items), Some(80))
    items += soup
    assertEquals(discount.calculate(items), Some(120))
    items += bread
    assertEquals(discount.calculate(items), Some(160))
  }

  test(
    "BundledDiscountForSecondItem should calculate the discount based on the price of itemOnDiscount",
  ) {
    val soup = CountableItem("Soup", 65)
    for (breadPrice, expectedOneBundle, expectedTwoBundles) <- Array(
        (80, Some(40), Some(80)),
        (55, Some(28), Some(55)),
        (100, Some(50), Some(100)),
      )
    do
      val bread = CountableItem("Bread", breadPrice)
      val discount = BundledDiscountForSecondItem(
        name = "Half-price bread per tin of soup",
        itemToBuy = soup,
        quantityRequired = 1,
        itemOnDiscount = bread,
        maxQuantityDiscounted = 1,
        discountRatio = 0.5,
      )
      val oneBundle = List(soup, bread)
      val twoBundles = List(soup, soup, bread, bread)
      assertEquals(discount.calculate(oneBundle), expectedOneBundle)
      assertEquals(discount.calculate(twoBundles), expectedTwoBundles)
  }

  test(
    "BundledDiscountForSecondItem should calculate the discount based on the discountRatio",
  ) {
    val soup = CountableItem("Soup", 65)
    val bread = CountableItem("Bread", 80)
    val oneBundle = List(soup, bread)
    val twoBundles = List(soup, soup, bread, bread)
    for (discountRatio, expectedOneBundle, expectedTwoBundles) <- Array(
        (0.5f, Some(40), Some(80)),
        (1.0f, Some(80), Some(160)),
      )
    do
      val discount = BundledDiscountForSecondItem(
        name = "Half-price bread per tin of soup",
        itemToBuy = soup,
        quantityRequired = 1,
        itemOnDiscount = bread,
        maxQuantityDiscounted = 1,
        discountRatio = discountRatio,
      )
      assertEquals(discount.calculate(oneBundle), expectedOneBundle)
      assertEquals(discount.calculate(twoBundles), expectedTwoBundles)
  }
