package FileHandling;


import java.io.*;

public class ObjectFile {
    private ObjectInputStream input_stream;
    private ObjectOutputStream output_stream;

    public ObjectFile() {
    }

    public void openRead(String fileName) throws IOException{
        input_stream = new ObjectInputStream(new FileInputStream(fileName));
    }

    public void openWrite(String fileName) throws IOException{
        try {
            output_stream = new ObjectOutputStream(new FileOutputStream(fileName));
        } catch (IOException e) {
            System.out.println("Excepção encontrada na função openWrite para ficheiro " + fileName + ": " + e);
        }
    }

    public Object readsObject() {
        try {
            return (input_stream.readObject());
        } catch (IOException e) {
            System.out.println("Excepção encontrada na função readsObject: " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Excepção encontrada na função readsObject: " + e);
        }
        return null;
    }

    public void writesObject(Object o) {
        try {
            output_stream.writeObject(o);
        } catch (IOException e1) {
            System.out.println("Excepção encontrada na função writesObject: " + e1);
        }
    }

    public void closeWrite() {
        try {
            output_stream.close();
        } catch (IOException e) {
            System.out.println("Excepção encontrada na função closeWrite: "+ e);
        }
    }

    public void closeRead() {
        try {
            input_stream.close();
        } catch (IOException e){
            System.out.println("Excpção encontrada na função closeRead: " + e);
        }
    }
}
