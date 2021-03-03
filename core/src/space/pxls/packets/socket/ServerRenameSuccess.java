package space.pxls.packets.socket;

public class ServerRenameSuccess {
    public final String type = "rename_success";

    private final String newName;

    public ServerRenameSuccess(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }
}
