package tunafish2k.legacydoll;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;

public class ModConfig extends Config {
    public ModConfig() {
        super(new Mod("Legacy Doll", ModType.HUD), "legacy_doll.json");
        initialize();
    }
    @Switch(
            name = "Enabled",
            description = "Render the doll",
            size = OptionSize.DUAL
    )
    public static boolean dollEnabled = false;

    @Dropdown(
            name = "Side",
            description = "The side of the doll",
            options = {"Left", "Right"}
    )
    public static int side = 0;

    @Slider(
            name = "X Offset",
            description = "The space between the doll and the side border",
            min = 0, max = 200
    )
    public static int horizontalMargin = 22;
    @Slider(
            name = "Y Offset",
            description = "The space between the doll and the top border",
            min = 0, max = 200
    )
    public static int verticalMargin = 11;
    @Slider(
            name = "Scale",
            description = "The scale of the doll",
            min = 10, max = 180
    )
    public static int scale = 30;

    @Slider(
            name = "Width Multiplier",
            description = "Adjust visual width calculation (for right-side positioning)",
            min = 10, max = 30
    )
    public static int widthMultiplier = 15;  // 15 = 1.5x
}
