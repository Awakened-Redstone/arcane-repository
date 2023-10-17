```json
{
  "title": "Type: Item Storage",
  "icon": "arcane_repository:item_storage_type_page",
  "category": "arcane_repository:item_storage",
  "associated_items": [
    "arcane_repository:item_storage_type_page"
  ]
}
```

Item Storage Type Pages, as you might except, are used to create books that hold items.
On their own, they mostly function as more flexible bundles, 
but they also serve as the primary storage medium used by [Indexing Books](^arcane_repository:indexing/indexing_type_page).


<|item-spotlight@lavender:book_components|item=arcane_repository:item_storage_type_page|>

;;;;;

After creating an item storage book, you can add items to it from within your 
inventory by holding a stack in your cursor, and right-clicking on the book.
Similarly, you can extract items by clicking on it with an empty cursor.


To choose which item to extract, you can scroll through the book's contents using the scroll wheel.
The currently selected item will be displayed in the center of the tooltip.

;;;;;

<|item-spotlight@lavender:book_components|item=arcane_repository:repository_book{attribute_pages: ["arcane_repository:stacks_page"\, "arcane_repository:types_page"\, "arcane_repository:stacks_page"]\, color: 11206485\, catalyst: "minecraft:emerald"\, type_page: "arcane_repository:item_storage_type_page"\, occupied_types: 7\, occupied_stacks: 7\, attributes: {max_types: 8\, max_stacks: 8}\, Items: [{Item: {id: "minecraft:gold_ingot"\, Count: 1b}\, Count: 1}\, {Item: {id: "minecraft:leather"\, Count: 1b}\, Count: 1}\, {Item: {id: "minecraft:emerald"\, Count: 1b\, tag: {RepairCost: 0\, display: {Name: '{"text":"4 pages"}'}}}\, Count: 1}\, {Item: {id: "minecraft:diamond"\, Count: 1b}\, Count: 1}\, {Item: {id: "minecraft:amethyst_shard"\, Count: 1b}\, Count: 1}\, {Item: {id: "minecraft:netherite_ingot"\, Count: 1b}\, Count: 1}\, {Item: {id: "minecraft:lapis_lazuli"\, Count: 1b}\, Count: 1}]\, filters: {}}|>
Above is an example of a book that uses an Item Storage Type Page, holding 7 different items. 
*Hover over the item to see its tooltip.*

<recipe;arcane_repository:page/type/item_storage>

;;;;;

It should be noted that placing this type of book on a Lectern will allow you to set filters for the items it can hold.
This may come in handy when building a large-scale repository,
as it allows you to create dedicated books for some item types.


You'll be able to right-click items on the lectern to add them as filters,
and use shift-right-click to pick the book up again.