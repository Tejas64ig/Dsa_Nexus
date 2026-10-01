DSA Nexus
A futuristic Data Structures & Algorithms visualization workstation built with
pure Java (Swing + AWT + Graphics2D). No JavaFX, no external libraries, no image assets.
Everything - the glowing UI, the pseudo-3D bars and nodes, the particles and the cursor dragon -
is drawn in code.
Features
Area	What it does
Sorting	Pseudo-3D bars (front/top/side faces + shadow). Bubble, Selection and Insertion sort. Compared bars lift and glow, swapped bars bounce. Click/drag the bars to edit heights. "SORTING COMPLETE" banner + particle burst.
Linked List	Pseudo-3D nodes with animated dashed arrows, HEAD / NULL markers. Add Head, Add Tail, Remove Head, Traverse. Click a node to access it (scale, glow, sparkle).
Binary Search Tree	Insert / Search / Insert Random / Clear. Path is highlighted node by node; a new node flies out of its parent into place while the tree re-layouts smoothly. FOUND / NOT FOUND banner.
Graph	Click to add nodes, drag node to node to connect, Shift+drag to move, right-click to delete. BFS, DFS, Dijkstra (first node to last node) with animated traversal and a progressively revealed shortest path.
Cursor dragon	A small segmented creature that follows the mouse with smoothing, tilts with movement, breathes when idle and reacts to fast motion.
Particles	Fading cursor trail and completion bursts from a capped, reusable particle system.
UI polish	Animated network background, glass panels, hover/press button effects, sliding sidebar indicator, ~240 ms slide + fade screen transitions.
Requirements
JDK 11 or newer (developed against JDK 21). A JRE alone is not enough - you need `javac`.
A desktop environment (it opens a Swing window).
Run
Windows: double-click `run.bat`
macOS / Linux: `./run.sh` (run `chmod +x run.sh` first if needed)
Both scripts do:
```
javac -encoding UTF-8 -d out src/dsanexus/*.java
java -cp out dsanexus.Main
```
IntelliJ IDEA
Open the `DsaNexus` folder as a project (a `DsaNexus.iml` module file is included).
Make sure `src` is marked as the Sources Root.
Run `dsanexus.Main`.
> If IntelliJ shows *"Cannot resolve symbol View"* (or similar), a file is outside the
> `dsanexus` package. All `.java` files must be in `src/dsanexus/` and start with `package dsanexus;`.
Controls
Screen	Controls
Sidebar	Click Sorting / Linked List / Tree / Graph to switch screens
Sorting	Shuffle, Bubble / Selection / Insertion sort. Click or drag on bars to change heights (when idle)
Linked List	Add Tail, Add Head, Remove Head, Traverse. Click a node to access it
Tree	Type a number, then Insert or Search. Insert Random, Clear
Graph	Click empty space: add node. Drag node to node: connect. Shift + drag: move node. Right-click: delete node or edge. Buttons: BFS, DFS, Dijkstra, Reset Marks, Clear
Project structure
```
DsaNexus/
├── README.md
├── DsaNexus.iml
├── run.bat / run.sh
└── src/dsanexus/
    ├── Main.java            Window setup, wiring, single animation timer, global mouse tracking
    ├── App.java             Shared state (tickables, particles, dragon, overlay, mouse)
    ├── Theme.java           Colours + drawing/animation helpers (glass, damp, dashPhase, ...)
    ├── Tickable.java        Interface for anything animated
    ├── Background.java      Animated particle-network background
    ├── Sidebar.java         Category list with sliding indicator
    ├── Stage.java           CardLayout host + slide/fade transitions
    ├── View.java            Base class for a screen (toolbar, canvas, completion banner)
    ├── SortView.java        Sorting visualization
    ├── ListView.java        Linked list visualization
    ├── TreeView.java        BST visualization
    ├── GraphView.java       Graph visualization (BFS / DFS / Dijkstra)
    ├── Steps.java           Timed step sequencer (replays algorithms without threads)
    ├── HoverButton.java     Animated button
    ├── ParticleSystem.java  Capped particle pool
    ├── Dragon.java          Cursor-following creature
    └── Overlay.java         Click-through glass pane (draws particles + dragon)
```
How it works
One timer. A single `javax.swing.Timer` (~60 FPS) calls `tick(dt)` on every `Tickable`
(views, buttons, sidebar, background, dragon), updates the particles and repaints. No extra threads.
Algorithms never block the UI. Each algorithm is precomputed into a list of steps, and
`Steps` replays them with delays, so animation and interaction stay responsive.
Glass-pane overlay. The dragon and particles are drawn on the frame's glass pane, which
overrides `contains()` to return `false`, so all mouse events pass straight through to the UI.
Pseudo-3D is faked with Graphics2D: extruded rounded rectangles, parallelogram top/side
faces, radial-gradient spheres, drop shadows and layered transparency.
Smooth motion uses exponential smoothing (`Theme.damp`) for nodes, bars, the dragon and the sidebar.
Performance. Particle count is capped (350), expired particles are removed with
swap-removal, and hidden screens skip their update/render work.
Adding a new screen
Create `MyView.java` in `src/dsanexus/`, `extends View`, implement `update(double dt)` and
`render(Graphics2D g, int w, int h)`. Add toolbar controls with `addTool(new HoverButton(...))`.
Register it in `Main.start()`: `stage.add(new MyView(), "My Screen");`
Add `"My Screen"` to the string array passed to `new Sidebar(...)`.
Known limitations
Only Sorting, Linked List, Tree and Graph screens exist. Searching, Stack, Queue and a
pathfinding grid with wall painting are not built yet.
The Linked List screen currently supports a singly linked list only (head/tail add, head remove, traverse).
Dijkstra always runs from the first node to the most recently created node.
The whole window repaints every frame because the background is always animating.
Roadmap
Linked List upgrade: Singly / Doubly / Circular / Circular Doubly lists with insert and
delete at head, tail and index, forward/backward/circular traversal, New and Clear buttons,
an index shown on every node, and invalid-index validation.
Searching (linear/binary), Stack, Queue and pathfinding grid screens.
Troubleshooting
Problem	Fix
`javac` not found	Install a JDK (not just a JRE) and make sure it is on your `PATH`
`IllegalArgumentException: negative dash phase`	Use `Theme.dashPhase(...)` for animated dashes; `BasicStroke` rejects negative phases
Special symbols look like boxes	Compile with `-encoding UTF-8` (the run scripts already do)
Clicks seem blocked	Ensure `Overlay.contains()` returns `false` (it does by default)
"Cannot resolve symbol" in IntelliJ	Put every file in `src/dsanexus/` with `package dsanexus;` and mark `src` as Sources Root
License
Educational / personal project. Use and modify freely.
