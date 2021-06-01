import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class treeload {

	public static void main(String[] args) throws IOException {
		
		//initialize array list to get and sort all records
		ArrayList<Record> records = new ArrayList<Record>();
		ArrayList<leafNode> leafNodes = new ArrayList<leafNode>();
		ArrayList<internalNode> indexNodes = new ArrayList<internalNode>();
		
		 // check for correct number of arguments
        if (args.length != constants.TREELOAD_ARG_COUNT) {
            System.out.println("Error: Incorrect number of arguments were input" + args.length);
            return;
        }
        
        //get page size of heap to be read into
        int pageSize = Integer.parseInt(args[constants.TREELOAD_ARG_COUNT - 1]);

        String datafile = "heap." + pageSize;
        long startTime = 0;
        long finishTime = 0;
        int numBytesInOneRecord = constants.INDEX_RECORD_SIZE;
        int numBytesInSdtnameField = constants.STD_NAME_SIZE;
        int numRecordsPerPage = pageSize/numBytesInOneRecord;
        byte[] page = new byte[pageSize];
        int pageNum = 0;
        FileInputStream inStream = null;
        FileOutputStream outputStream = null;
        ByteArrayOutputStream byteOutputStream = null;
        DataOutputStream dataOutput = null;
        
      //write out nodes into index file
        String outputFileName = "index." + pageSize;
        int numRecordsLoaded = 0;
        int numberOfPagesUsed = 0;

        try {
            inStream = new FileInputStream(datafile);
            int numBytesRead = 0;
            startTime = System.nanoTime();
            // Create byte arrays for sdt name field
            byte[] sdtnameBytes = new byte[numBytesInSdtnameField];

            // until the end of the binary file is reached
            while ((numBytesRead = inStream.read(page)) != -1) {
                // Process each record in page
                for (int i = 0; i < numRecordsPerPage; i++) {
                	
                    // get record's SdtName (field is located at multiples of the total record byte length)
                    System.arraycopy(page, (i*numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);
                	
                    // Check if field is empty; if so, end of all records found (packed organisation)
                    if (sdtnameBytes[0] == 0) {
                        // can stop checking records
                        break;
                    }

                    String sdtNameString = new String(sdtnameBytes);
                    int position = i * numBytesInOneRecord;
                    
                    Record newRecord = new Record(sdtNameString, pageNum, position);
                    
                    //add all new records into list
                    records.add(newRecord);
                    
                    
                }
                pageNum++;
            }

            finishTime = System.nanoTime();
            
            //sort the record
            records.sort(new sdtNameSorter());
            
            //bulk import records into leaf nodes
            for(int i = 0 ; i < records.size() - 4; i += 4) {
            	
            	//create leaf nodes to store all records
            	//each leaf node has 4 records
            	leafNode newNode = new leafNode(records.get(i), records.get(i+1), records.get(i+2), records.get(i+3));
            	
            	//add leaf node into list of all leaf nodes
            	leafNodes.add(newNode);
            	
            }
            
            //link the leaf nodes
            for(int i = 0 ; i < leafNodes.size() - 1; i++) {
            	
            	//if first leaf node, no need to set previous leaf node, only set next leaf node
            	if(i == 0) {
            		
            		leafNodes.get(i).setNextNode(leafNodes.get(i+1));
            	}
            	
            	//else, set both previous leaf node and next node as it is a doubly linked list setup for the tree index
            	else {
            		leafNodes.get(i).setPrevNode(leafNodes.get(i-1));
            		leafNodes.get(i).setNextNode(leafNodes.get(i+1));
            	}
            }
            
            //create internal nodes including root node
            //determine which leafnodes to be pushed as internal nodes 
            //4 keys will acts as internal node 
            
            String key1 = records.get((int) (records.size()*constants.KEY_ONE - 1)).getSdtName();
            String key2 = records.get((int) (records.size()*constants.KEY_TWO - 1)).getSdtName();
            String key3 = records.get((int) (records.size()*constants.KEY_THREE - 1)).getSdtName();
            String key4 = records.get((int) (records.size()*constants.KEY_FOUR - 1)).getSdtName();
            
            // store all internal nodes
            internalNode node1 = new internalNode(key1);
            internalNode node2 = new internalNode(key2);
            internalNode node3 = new internalNode(key3);
            internalNode node4 = new internalNode(key4);
            
            indexNodes.add(node1);
            indexNodes.add(node2);
            indexNodes.add(node3);
            indexNodes.add(node4);
            
            
            //write out index file in bytes
            outputStream = new FileOutputStream(outputFileName, true);
            byteOutputStream = new ByteArrayOutputStream();
            dataOutput = new DataOutputStream(byteOutputStream);
            
            for(Record r : records) {
            	
            	dataOutput.writeBytes(getStringOfLength(r.getSdtName(), constants.STD_NAME_SIZE));
            	dataOutput.writeInt(r.getPageNum());
            	dataOutput.writeInt(r.getPosition());
            	
            	numRecordsLoaded++;
            	
                // check if a new page is needed
                if (numRecordsLoaded % numRecordsPerPage == 0) {
                    dataOutput.flush();
                    
                    // Get the byte array of loaded records, copy to an empty page and writeout
                    byte[] page2 = new byte[pageSize];
                    byte[] records2 = byteOutputStream.toByteArray();
                    int numberBytesToCopy = byteOutputStream.size();
                    System.arraycopy(records2, 0, page2, 0, numberBytesToCopy);
                    writeOut(outputStream, page2);
                    numberOfPagesUsed++;
                    byteOutputStream.reset();
                }
                
            }
            
            // At end of csv, check if there are records in the current page to be written out
            if (numRecordsLoaded % numRecordsPerPage != 0) {
                dataOutput.flush();
                byte[] page2 = new byte[pageSize];
                byte[] records2 = byteOutputStream.toByteArray();
                int numberBytesToCopy = byteOutputStream.size();
                System.arraycopy(records2, 0, page2, 0, numberBytesToCopy);
                writeOut(outputStream, page2);
                numberOfPagesUsed++;
                byteOutputStream.reset();
            }
             
            
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found " + e.getMessage());
        }
        catch (IOException e) {
            System.err.println("IO Exception " + e.getMessage());
        }
        finally {

            if (inStream != null) {
                inStream.close();
            }
            if (dataOutput != null) {
                dataOutput.close();
            }
            if (byteOutputStream != null) {
                byteOutputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        
        long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;
        System.out.println(numberOfPagesUsed);
        System.out.println("Time taken: " + timeInMilliseconds + " ms");
    }
	
    // Writes out a byte array to file using a FileOutputStream
    public static void writeOut(FileOutputStream stream, byte[] byteArray)
            throws FileNotFoundException, IOException {

        stream.write(byteArray);
    }

    // Returns a whitespace padded string of the same length as parameter int length
    public static String getStringOfLength(String original, int length) {

        int lengthDiff = length - original.length();

        // Check difference in string lengths
        if (lengthDiff == 0) {
            return original;
        }
        else if (lengthDiff > 0) {
            // if original string is too short, pad end with whitespace
            StringBuilder string = new StringBuilder(original);
            for (int i = 0; i < lengthDiff; i++) {
                string.append(" ");
            }
            return string.toString();
        }
        else {
            // if original string is too long, shorten to required length
            return original.substring(0, length);
        }
    }

}

