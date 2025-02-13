import adthena.task.shoppingcart.ShoppingCart
import adthena.task.data.ExampleCatalogue

@main def shoppingCart(itemNames: String*): Unit =
  val shoppingCart = ShoppingCart(ExampleCatalogue)
  itemNames.foreach(
    shoppingCart.addItemFromString(_),
  )
  shoppingCart.checkout()
