# Tako Libs

Reusable client-side UI, layout, editor history and runtime state components for Fabric mods.

## Build

Install the SVG rasterizer tool once before building:

```text
py -3 -m pip install -r tools/requirements-svg.txt
```

SVG sources under `assets/icons/svg` are converted to ignored PNG textures automatically by Gradle. To run the converter manually:

```text
py -3 scripts/svg_to_png.py
```

```text
gradlew.bat build
gradlew.bat publishToMavenLocal
```

Artifacts are published per Minecraft version:

```text
dev.tako:tako-libs-fabric-1.20.4:0.2.2
dev.tako:tako-libs-fabric-1.21.1:0.2.2
```

## Layout

`TakoFlexContainer` arranges children in a row or column. `TakoGridContainer` places children into explicit row and column tracks; both apply bounds to `TakoLayoutable` children and support nesting.

```kotlin
TakoFlexContainer<TakoLayoutable>(direction = TakoFlexDirection.ROW, gap = 8)
    .add(label, TakoSizeSpec.Fixed(64))
    .add(value, TakoSizeSpec.Weight())
    .layout(TakoBounds(16, 16, 240, 20))

TakoGridContainer<TakoLayoutable>(
    columns = listOf(TakoSizeSpec.Fixed(64), TakoSizeSpec.Weight()),
    rows = listOf(TakoSizeSpec.Fixed(20), TakoSizeSpec.Weight()),
    columnGap = 8,
    rowGap = 4
)
    .add(label, row = 0, column = 0)
    .add(value, row = 0, column = 1)
    .layout(TakoBounds(16, 16, 240, 80))
```
