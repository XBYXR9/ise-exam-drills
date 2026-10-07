package de.tum.ise;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
 * REST IMPLEMENTATION TEMPLATE  (Resource + Service + model in one file; SPLIT into 3 files when copying)
 * Rename:  Item -> Ticket,  ItemResource -> TicketResource,  /items -> /tickets,  itemId -> ticketId ...
 *
 * BEFORE YOU WRITE ANYTHING:
 *   - Does @RequestMapping on the class already carry a path? If the skeleton only sets consumes/produces,
 *     EVERY method must spell the full path:  @PostMapping("/items").
 *   - Copy the status-code TABLE from the task. POST created is 200 in the 2026 exam, not 201.
 *   - The {name} in the mapping and the Java parameter name must be IDENTICAL, or you get HTTP 500.
 *   - Keep layers closed: Resource -> Service -> (Repository). Never skip a layer.
 *   - git add . && git commit && git push AFTER EVERY ENDPOINT.
 */

// ===================== model (usually given) =====================
class Item {
    private Long id;
    private String name;
    private int quantity;

    public Item() { }                                              // Jackson needs the no-arg constructor
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

// ===================== service: logic over a plain list =====================
@Service
class ItemService {
    private final List<Item> items = new ArrayList<>();
    private long nextId = 1L;

    public List<Item> getAllItems() {
        return new ArrayList<>(items);                              // a COPY, not the internal list
    }

    /** With a filter (query-parameter task): onlyAvailable=true keeps quantity > 0. */
    public List<Item> getAllItems(boolean onlyAvailable) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (!onlyAvailable || item.getQuantity() > 0) {
                result.add(item);
            }
        }
        return result;
    }

    public Optional<Item> findItemById(Long itemId) {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {                      // equals(), NEVER == on Long
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public Item saveItem(Item item) {
        if (item.getId() == null) {                                 // CREATE
            item.setId(nextId);                                     // assign, THEN increment
            nextId++;
            items.add(item);
            return item;                                            // never return null on success
        }
        Optional<Item> stored = findItemById(item.getId());         // UPDATE
        if (stored.isEmpty()) {
            return null;                                            // must FAIL, never silently create
        }
        Item existing = stored.get();
        existing.setName(item.getName());                           // copy EVERY field
        existing.setQuantity(item.getQuantity());
        return existing;                                            // return the STORED object
    }

    public void deleteItem(Long itemId) {
        items.removeIf(item -> item.getId().equals(itemId));        // never throws for unknown ids
    }
}

// ===================== resource: HTTP + validation only =====================
@RestController
@RequestMapping("/items")
class ItemResource {

    private final ItemService itemService;

    public ItemResource(ItemService itemService) {                  // constructor injection
        this.itemService = itemService;
    }

    // POST: body carries the object, the SERVER assigns the id -> 400 if one is supplied
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        if (item.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(itemService.saveItem(item));
    }

    // GET one: identity in the PATH, 404 when absent
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@PathVariable Long itemId) {
        return itemService.findItemById(itemId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // GET all: a filter is a QUERY parameter, optional with a default
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems(
            @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") boolean onlyAvailable) {
        return ResponseEntity.ok(itemService.getAllItems(onlyAvailable));
        // no filter in the task? -> no parameter: return ResponseEntity.ok(itemService.getAllItems());
    }

    // PUT: check order = 400 (request shape) -> 404 (exists?) -> 409 (state, only if the task says so)
    @PutMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable Long itemId, @RequestBody Item item) {
        if (!Objects.equals(itemId, item.getId())) {                // also covers a body WITHOUT an id
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Item updated = itemService.saveItem(item);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE: 204 always (read the table: some tasks want 200 -> ResponseEntity.ok().build())
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // ---------- Quick reference ----------
    // 200 ResponseEntity.ok(body) | 204 .noContent().build() | 400 .badRequest().build() | 404 .notFound().build()
    // or throw new ResponseStatusException(HttpStatus.X) -- use whichever the skeleton already imports.
    // Optional param: @RequestParam(required = false)  |  with default: @RequestParam(defaultValue = "x")
    // Several path variables: @GetMapping("/{a}/sub/{b}") + @PathVariable Long a, @PathVariable Long b
}
