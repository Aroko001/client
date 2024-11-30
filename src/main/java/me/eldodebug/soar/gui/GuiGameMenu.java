package me.eldodebug.soar.gui;

import me.eldodebug.soar.Glide;
import me.eldodebug.soar.management.nanovg.NanoVGManager;
import me.eldodebug.soar.management.nanovg.font.Fonts;
import me.eldodebug.soar.management.nanovg.font.Icon;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.easing.EaseInOutCirc;
import me.eldodebug.soar.utils.buffer.ScreenAnimation;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.render.BlurUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import org.lwjgl.input.Keyboard;

import java.awt.*;


public class GuiGameMenu extends GuiScreen {

    // yo eldo im sorry for this mess xd -breadcat
    // Todo: translate text

    private Animation introAnimation;
    private final ScreenAnimation screenAnimation = new ScreenAnimation();
    private int x, y, width, height, centre, scaledWidth, scaledHeight;

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);

        scaledWidth = sr.getScaledWidth();
        scaledHeight = sr.getScaledHeight();
        centre = scaledWidth / 2;
        x = centre - 90;
        y = (scaledHeight / 2) - 110;
        width = 180;
        height = 220;

        introAnimation = new EaseInOutCirc(150, 1.0F);
        introAnimation.setDirection(Direction.FORWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BlurUtils.drawBlurScreen(20);
        NanoVGManager nvg = Glide.getInstance().getNanoVGManager();
        screenAnimation.wrap(() -> drawNanoVG(nvg), x, y, width, height, 2 - introAnimation.getValueFloat(), Math.min(introAnimation.getValueFloat(), 1), false);
        if(introAnimation.isDone(Direction.BACKWARDS)) {
               this.mc.displayGuiScreen(null);
               this.mc.setIngameFocus();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawNanoVG(NanoVGManager nvg) {
        nvg.drawRect(-5, -5, scaledWidth + 10, scaledHeight + 10,  new Color(0,0,0, 140));
        nvg.drawText(Icon.ARROW_LEFT, x, y + 5, new Color(255,255,255, 140),11, Fonts.ICON);
        nvg.drawCenteredText("Game Menu", centre, y + 5,  new Color(255,255,255, 200), 13, Fonts.MEDIUM);

        float standardPadding = 29.5f;
        float offset = 29.5F;
        drawButton(nvg, "Minecraft Options", Icon.SLIDERS, offset);
        offset += standardPadding;
        if(this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic()){
            drawButton(nvg, "Open to Lan", Icon.USERS, offset);
        } else {
            drawButton(nvg, "Edit Hud", Icon.LAYOUT, offset);
        }
        offset += standardPadding;
        drawButton(nvg, "Statistics", Icon.ARCHIVE, offset);
        offset += standardPadding;
        drawButton(nvg, "Achievements", Icon.MAP, offset);
        offset += standardPadding;
        drawButton(nvg, "Open Glide Menu", Icon.SOAR, offset);
        offset += standardPadding;
        drawButton(nvg, !this.mc.isIntegratedServerRunning() ? "Disconnect" : "Return to Main Menu", Icon.LOGOUT, offset);
    }

    private void drawButton(NanoVGManager nvg, String s, String i, Float offset){
        nvg.drawRoundedRect(x, y + offset, width , 22, 6, new Color(230, 230, 230, 80));
        float startX = (nvg.getTextWidth(s, 9.5F, Fonts.MEDIUM) + 14) /2;
        nvg.drawText(i, centre - startX, y + offset + 5.5F, Color.WHITE, 9.5F, Fonts.ICON);
        nvg.drawText(s, centre - startX + 14, y + offset + 7F, Color.WHITE, 9.5F, Fonts.MEDIUM);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!(mouseButton == 0)) {return;}
        float standardPadding = 29.5f;
        float offset = standardPadding;

        if (MouseUtils.isInside(mouseX, mouseY, x, y + offset, width, 22)){
            introAnimation.setDirection(Direction.BACKWARDS);
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        offset += standardPadding;
        if (MouseUtils.isInside(mouseX, mouseY, x, y + offset, width, 22)){
            if(this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic()){
                this.mc.displayGuiScreen(new GuiShareToLan(this));
            } else {
                mc.displayGuiScreen(new GuiEditHUD(false));
            }
        }
        offset += standardPadding;
        if (MouseUtils.isInside(mouseX, mouseY, x, y + offset, width, 22)) {
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
        }
        offset += standardPadding;
        if (MouseUtils.isInside(mouseX, mouseY, x, y + offset, width, 22)) {
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
        }
        offset += standardPadding;
        if (MouseUtils.isInside(mouseX, mouseY, x, y + offset, width, 22)) {
            mc.displayGuiScreen(Glide.getInstance().getApi().getModMenu());
        }
        offset += standardPadding;
        if (MouseUtils.isInside(mouseX, mouseY, x, y + offset, width, 22)) {
            boolean flag = this.mc.isIntegratedServerRunning();
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);

            if (flag) {
                this.mc.displayGuiScreen(new GuiMainMenu());
            } else {
                this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }
        }
        if (!MouseUtils.isInside(mouseX, mouseY, x, y + standardPadding, width, offset - standardPadding + 22)) {
            introAnimation.setDirection(Direction.BACKWARDS);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            introAnimation.setDirection(Direction.BACKWARDS);
        }
    }
}