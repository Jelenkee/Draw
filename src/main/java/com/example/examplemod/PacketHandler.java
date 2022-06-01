package com.example.examplemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.io.*;
import java.util.Optional;

public class PacketHandler {
    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ExampleMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        INSTANCE.registerMessage(nextID(), M.class, M::encode, buf -> {
            M m = new M();
            m.decode(buf);
            return m;
        }, (m, contextSupplier) -> m.handleMessage(m, contextSupplier.get()), Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static int nextID() {
        return ID++;
    }

    public static class M extends AbstractMessage {

        public enum Action {
            CREATE_DRAW, DRAW_DRAW;

            private static Action[] array = Action.values();

            public static Action lookup(int index) {
                return array[index];
            }
        }

        public M() {

        }

        public M(Action action, String name, Drawing drawing) {
            this.nbt.putInt("action", action.ordinal());
            this.nbt.putString("name", name);
            this.nbt.put("drawing", drawing.serializeNBT());
        }

        @Override
        public void handleMessage(ServerPlayer player) {
            Action action = Action.lookup(this.nbt.getInt("action"));
            Drawing drawing = new Drawing();
            drawing.deserializeNBT((LongArrayTag) this.nbt.get("drawing"));
            String name = this.nbt.getString("name");
            System.out.println(System.currentTimeMillis());
        }
    }

    public static abstract class AbstractMessage {
        protected CompoundTag nbt = new CompoundTag();

        public AbstractMessage() {
        }

        public AbstractMessage(CompoundTag nbt) {
            this.nbt = nbt;
        }

        public final void encode(FriendlyByteBuf buffer) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            try {
                NbtIo.write(nbt, dos);
            } catch (IOException e) {
            }
            buffer.writeByteArray(baos.toByteArray());
        }

        public final void decode(FriendlyByteBuf buffer) {
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer.readByteArray());
            DataInputStream dis = new DataInputStream(bais);
            try {
                nbt = NbtIo.read(dis);
            } catch (IOException e) {
            }
        }

        public final void handleMessage(AbstractMessage message, NetworkEvent.Context context) {
            nbt = message.nbt;
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                handleMessage(player);
                context.setPacketHandled(true);
            });
        }

        public abstract void handleMessage(ServerPlayer player);

    }


}
