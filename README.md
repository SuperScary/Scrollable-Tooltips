# Scrollable Tooltips

A Minecraft mod that makes long tooltips scrollable using the mouse wheel. Supports **Minecraft 1.21.1** on **NeoForge**, **Forge**, and **Fabric**.

## Features

- **Scrollable Tooltips**: When a tooltip's height exceeds the configurable maximum, scroll through the content using the mouse wheel
- **Visual Indicators**: Subtle fade indicators at the top/bottom show when more content exists above or below
- **Automatic Reset**: Scroll position resets when hovering a different item, changing screens, or after a short timeout
- **Mod Compatibility**: Works with other mods that add tooltip lines – does not replace tooltip generation, only affects rendering

## How It Works

- Hover over an item with a long tooltip
- Use the **mouse wheel** to scroll through the tooltip content while holding the tooltip key
- A small arrow indicator appears at the top/bottom when content is scrolled out of view

## Configuration

Configuration files are created automatically on first launch:

| Loader   | Config Location                         |
|----------|-----------------------------------------|
| Fabric   | `config/scrollabletooltips.json`        |
| Forge    | `config/scrollabletooltips-client.toml` |
| NeoForge | `config/scrollabletooltips-client.toml` |

### Config Options

| Option               | Default | Description                                                             |
|----------------------|---------|-------------------------------------------------------------------------|
| `enabled`            | `true`  | Enable/disable the scrollable tooltips feature                          |
| `maxTooltipHeightPx` | `180`   | Maximum tooltip height in pixels before scrolling becomes available     |
| `scrollSpeedPx`      | `10`    | Number of pixels to scroll per mouse wheel notch                        |
| `onlyWhenOverflowed` | `true`  | Only enable scrolling when the tooltip exceeds the maximum height       |
| `showIndicators`     | `true`  | Show visual indicators when content exists above/below the visible area |

## Building

This project uses the MultiLoader template. To build for all platforms:

```bash
./gradlew build
```

## Technical Details

### Compatibility

- **Minecraft Version**: 1.21.1
- **Java Version**: 21
- **Supported Loaders**: Fabric, Forge, NeoForge

### Implementation Notes

- Uses client-side mixins to hook into tooltip rendering (`GuiGraphics.renderTooltipInternal`)
- Applies scissor clipping to the tooltip content area
- Uses pose translation to offset tooltip content by the scroll amount
- Mouse scroll events are intercepted only when a tooltip is actively being rendered and is overflowed
- Scroll state is tracked per-screen and resets when:
  - The hovered item changes
  - The screen changes
  - The tooltip hasn't been shown for 500ms

### Known Limitations

- Tooltips that use custom rendering (bypassing standard tooltip APIs) may not be scrollable
- The scroll position is calculated based on the tooltip's initial render position
- Very rapid tooltip switching may occasionally show incorrect scroll state for a frame

## License

Apache-2.0 - See [LICENSE.txt](LICENSE.txt)

## Credits

- SuperScary - Author
