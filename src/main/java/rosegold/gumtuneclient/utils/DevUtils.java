package rosegold.gumtuneclient.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

public class DevUtils {
    public static String getEntityData(Entity entity) {
        NBTTagCompound entityData = new NBTTagCompound();
        entity.writeToNBT(entityData);
        StringBuilder stringBuilder = new StringBuilder();
        if (stringBuilder.length() > 0) {
            stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
        }

        stringBuilder.append("Class: ").append(entity.getClass().getSimpleName()).append(System.lineSeparator());
        if (entity.hasCustomName() || EntityPlayer.class.isAssignableFrom(entity.getClass())) {
            stringBuilder.append("Name: ").append(entity.getName()).append(System.lineSeparator());
        }

        stringBuilder.append("NBT Data:").append(System.lineSeparator());
        stringBuilder.append(prettyPrintNBT(entityData));

        return stringBuilder.toString();
    }

    public static String getTileEntityData(TileEntity tileEntity) {
        return prettyPrintNBT(tileEntity.getTileData());
    }

    public static void copyStringToClipboard(String string, String successMessage) {
        writeToClipboard(string, successMessage);
    }

    private static void writeToClipboard(String text, String successMessage) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection output = new StringSelection(text);

        try {
            clipboard.setContents(output, output);
            ModUtils.sendMessage(successMessage);
        } catch (IllegalStateException exception) {
            ModUtils.sendMessage("&cClipboard not available!");
        }
    }

    public static void copyNBTTagToClipboard(NBTBase nbtTag, String message) {
        if (nbtTag == null) {
            ModUtils.sendMessage("&cThis item has no NBT data!");
            return;
        }
        writeToClipboard(prettyPrintNBT(nbtTag), message);
    }

    public static String prettyPrintNBT(NBTBase nbt) {
        final String INDENT = "    ";

        int tagID = nbt.getId();
        StringBuilder stringBuilder = new StringBuilder();

        // Determine which type of tag it is.
        if (tagID == Constants.NBT.TAG_END) {
            stringBuilder.append('}');

        } else if (tagID == Constants.NBT.TAG_BYTE_ARRAY || tagID == Constants.NBT.TAG_INT_ARRAY) {
            stringBuilder.append('[');
            if (tagID == Constants.NBT.TAG_BYTE_ARRAY) {
                NBTTagByteArray nbtByteArray = (NBTTagByteArray) nbt;
                byte[] bytes = nbtByteArray.getByteArray();

                for (int i = 0; i < bytes.length; i++) {
                    stringBuilder.append(bytes[i]);

                    // Don't add a comma after the last element.
                    if (i < (bytes.length - 1)) {
                        stringBuilder.append(", ");
                    }
                }
            } else {
                NBTTagIntArray nbtIntArray = (NBTTagIntArray) nbt;
                int[] ints = nbtIntArray.getIntArray();

                for (int i = 0; i < ints.length; i++) {
                    stringBuilder.append(ints[i]);

                    // Don't add a comma after the last element.
                    if (i < (ints.length - 1)) {
                        stringBuilder.append(", ");
                    }
                }
            }
            stringBuilder.append(']');

        } else if (tagID == Constants.NBT.TAG_LIST) {
            NBTTagList nbtTagList = (NBTTagList) nbt;

            stringBuilder.append('[');
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
                NBTBase currentListElement = nbtTagList.get(i);

                stringBuilder.append(prettyPrintNBT(currentListElement));

                // Don't add a comma after the last element.
                if (i < (nbtTagList.tagCount() - 1)) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(']');

        } else if (tagID == Constants.NBT.TAG_COMPOUND) {
            NBTTagCompound nbtTagCompound = (NBTTagCompound) nbt;

            stringBuilder.append('{');
            if (!nbtTagCompound.hasNoTags()) {
                Iterator<String> iterator = nbtTagCompound.getKeySet().iterator();

                stringBuilder.append(System.lineSeparator());

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    NBTBase currentCompoundTagElement = nbtTagCompound.getTag(key);

                    stringBuilder.append(key).append(": ").append(
                            prettyPrintNBT(currentCompoundTagElement));

                    if (key.contains("backpack_data") && currentCompoundTagElement instanceof NBTTagByteArray) {
                        try {
                            NBTTagCompound backpackData = CompressedStreamTools.readCompressed(new ByteArrayInputStream(((NBTTagByteArray)currentCompoundTagElement).getByteArray()));

                            stringBuilder.append(",").append(System.lineSeparator());
                            stringBuilder.append(key).append("(decoded): ").append(
                                    prettyPrintNBT(backpackData));
                        } catch (IOException e) {
                            System.out.println("Couldn't decompress backpack data into NBT, skipping!");
                            e.printStackTrace();
                        }
                    }

                    // Don't add a comma after the last element.
                    if (iterator.hasNext()) {
                        stringBuilder.append(",").append(System.lineSeparator());
                    }
                }

                // Indent all lines
                String indentedString = stringBuilder.toString().replaceAll(System.lineSeparator(), System.lineSeparator() + INDENT);
                stringBuilder = new StringBuilder(indentedString);
            }

            stringBuilder.append(System.lineSeparator()).append('}');
        }
        // This includes the tags: byte, short, int, long, float, double, and string
        else {
            stringBuilder.append(nbt);
        }

        return stringBuilder.toString();
    }
}
