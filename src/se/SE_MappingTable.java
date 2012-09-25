package se;

import java.util.ArrayList;

public class SE_MappingTable {

	private ArrayList<MappingRecord> mappingRecords;

	public SE_MappingTable() {
		super();
		this.mappingRecords = new ArrayList<MappingRecord>();
	}

	public SE_MappingTable(ArrayList<MappingRecord> mappingRecords) {
		super();
		this.mappingRecords = mappingRecords;
	}

	public ArrayList<MappingRecord> getMappingRecords() {
		return mappingRecords;
	}

	public void setMappingRecords(ArrayList<MappingRecord> mappingRecords) {
		this.mappingRecords = mappingRecords;
	}
	
	public void addMappingRecord(MappingRecord mappingRecord){
		this.mappingRecords.add(mappingRecord);
	}

	public MappingRecord getMappingRecord(int index){
		return this.mappingRecords.get(index);
	}
}