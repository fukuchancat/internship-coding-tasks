package com.mynetgear.fukuchan.internshipcodingtasks.price;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * uszips.csvを読み込んだ郵便番号情報
 */
@Getter
@Setter
public class ZipData {
	private String zip;
	private double lat;
	private double lng;
	private String city;
	private String state_id;
	private String state_name;
	private boolean zcta;
	private String parent_zcta;
	private int population;
	private double density;
	private String county_fips;
	private String county_name;
	private String all_county_weights;
	private boolean imprecise;
	private boolean military;
	private String timezone;

	public ZipData(String zip, double lat, double lng, String city, String state_id, String state_name, boolean zcta, String parent_zcta, int population, double density, String county_fips, String county_name, String all_county_weights, boolean imprecise, boolean military, String timezone) {
		this.zip = zip;
		this.lat = lat;
		this.lng = lng;
		this.city = city;
		this.state_id = state_id;
		this.state_name = state_name;
		this.zcta = zcta;
		this.parent_zcta = parent_zcta;
		this.population = population;
		this.density = density;
		this.county_fips = county_fips;
		this.county_name = county_name;
		this.all_county_weights = all_county_weights;
		this.imprecise = imprecise;
		this.military = military;
		this.timezone = timezone;
	}

	public static List<ZipData> load(Path path) throws IOException {
		return Files.lines(path).skip(1).map(line -> line.replace("\"", "")).map(line -> line.split(",")).map(split -> {
			String zip = split[0];
			double lat = Double.parseDouble(split[1]);
			double lng = Double.parseDouble(split[2]);
			String city = split[3];
			String state_id = split[4];
			String state_name = split[5];
			boolean zcta = Boolean.parseBoolean(split[6]);
			String parent_zcta = split[7];
			int population = Integer.parseInt(split[8]);
			double density = Double.parseDouble(split[9]);
			String county_fips = split[10];
			String county_name = split[11];
			String all_county_weights = split[12];
			boolean imprecise = Boolean.parseBoolean(split[13]);
			boolean military = Boolean.parseBoolean(split[14]);
			String timezone = split[15];

			return new ZipData(zip, lat, lng, city, state_id, state_name, zcta, parent_zcta, population, density, county_fips, county_name, all_county_weights, imprecise, military, timezone);
		}).collect(Collectors.toList());
	}
}
