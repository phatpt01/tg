package se;

import java.util.ArrayList;

public class MappingTable {

	ArrayList<MappingRecord> mappingTable;

	public MappingTable() {
		super();
	}

	public MappingTable(ArrayList<MappingRecord> mappingTable) {
		super();
		this.mappingTable = mappingTable;
	}

	public ArrayList<MappingRecord> getMappingTable() {
		return mappingTable;
	}

	public void setMappingTable(ArrayList<MappingRecord> mappingTable) {
		this.mappingTable = mappingTable;
	}
	
	public void addMappingRecord(MappingRecord mappingRecord){
		this.mappingTable.add(mappingRecord);
	}

	public MappingRecord getMappingRecord(int index){
		return this.mappingTable.get(index);
	}
}