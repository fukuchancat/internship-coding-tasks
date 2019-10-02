package com.mynetgear.fukuchan.internshipcodingtasks.price;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Price.csvを読み込んだ家の情報
 */
@Getter
@Setter
public class House {
	private String id;
	private LocalDateTime date;
	private int price;
	private int bedrooms;
	private double bathrooms;
	private int sqftLiving;
	private int sqftLot;
	private double floors;
	private int waterfront;
	private int view;
	private int condition;
	private int grade;
	private int sqftAbove;
	private int sqftBasement;
	private int yrBuilt;
	private int yrRenovated;
	private String zipcode;
	private double lat;
	private double lng;
	private int sqftLiving15;
	private int sqftLot15;

	public House(String id, LocalDateTime date, int price, int bedrooms, double bathrooms, int sqftLiving, int sqftLot, double floors, int waterfront, int view, int condition, int grade, int sqftAbove, int sqftBasement, int yrBuilt, int yrRenovated, String zipcode, double lat, double lng, int sqftLiving15, int sqftLot15) {
		this.id = id;
		this.date = date;
		this.price = price;
		this.bedrooms = bedrooms;
		this.bathrooms = bathrooms;
		this.sqftLiving = sqftLiving;
		this.sqftLot = sqftLot;
		this.floors = floors;
		this.waterfront = waterfront;
		this.view = view;
		this.condition = condition;
		this.grade = grade;
		this.sqftAbove = sqftAbove;
		this.sqftBasement = sqftBasement;
		this.yrBuilt = yrBuilt;
		this.yrRenovated = yrRenovated;
		this.zipcode = zipcode;
		this.lat = lat;
		this.lng = lng;
		this.sqftLiving15 = sqftLiving15;
		this.sqftLot15 = sqftLot15;
	}

	/**
	 * @return リノベーションされた年、リノベーションされてない場合は建築された年
	 */
	public int yrElapsed() {
		return yrRenovated == 0 ? yrBuilt : yrRenovated;
	}

	/**
	 * @return 売り出されたときのエポック秒
	 */
	public long secSaled() {
		return date.toEpochSecond(ZoneOffset.UTC);
	}

	public static List<House> load(Path path) throws IOException {
		return Files.lines(path).skip(1).map(line -> line.replace("\"", "")).map(line -> line.split(",")).map(split -> {
			String id = split[0];
			LocalDateTime date = LocalDateTime.parse(split[1], DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
			int price = Integer.parseInt(split[2]);
			int bedrooms = Integer.parseInt(split[3]);
			double bathrooms = Double.parseDouble(split[4]);
			int sqftLiving = Integer.parseInt(split[5]);
			int sqftLot = Integer.parseInt(split[6]);
			double floors = Double.parseDouble(split[7]);
			int waterfront = Integer.parseInt(split[8]);
			int view = Integer.parseInt(split[9]);
			int condition = Integer.parseInt(split[10]);
			int grade = Integer.parseInt(split[11]);
			int sqftAbove = Integer.parseInt(split[12]);
			int sqftBasement = Integer.parseInt(split[13]);
			int yrBuilt = Integer.parseInt(split[14]);
			int yrRenovated = Integer.parseInt(split[15]);
			String zipcode = split[16];
			double lat = Double.parseDouble(split[17]);
			double lng = Double.parseDouble(split[18]);
			int sqftLiving15 = Integer.parseInt(split[19]);
			int sqftLot15 = Integer.parseInt(split[20]);

			return new House(id, date, price, bedrooms, bathrooms, sqftLiving, sqftLot, floors, waterfront, view, condition, grade, sqftAbove, sqftBasement, yrBuilt, yrRenovated, zipcode, lat, lng, sqftLiving15, sqftLot15);
		}).collect(Collectors.toList());
	}
}
