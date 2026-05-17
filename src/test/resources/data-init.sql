use public;

delete from stop;
delete from tap;

-- Bus Stop and Zones for "Victoria Transport Company" i.e. bus_company_id = 1
insert into stop(id, name, bus_company_id, zone) values(1, 'Melbourne William St - Bourke St', 1, 1);
insert into stop(id, name, bus_company_id, zone) values(2, 'Melbourne Exhibition St - Bourke St', 1, 1);
insert into stop(id, name, bus_company_id, zone) values(3, 'Southbank', 1, 1);
insert into stop(id, name, bus_company_id, zone) values(4, 'Sunshine', 1, 2);
insert into stop(id, name, bus_company_id, zone) values(5, 'Ballarat', 1, 3);

-- Bus Stop and Zones for "Buses Australia Company" i.e. bus_company_id = 2
insert into stop(id, name, bus_company_id, zone) values(6, 'Melbourne William St - Bourke St', 2, 1);
insert into stop(id, name, bus_company_id, zone) values(7, 'Southbank', 2, 2);
insert into stop(id, name, bus_company_id, zone) values(8, 'Frankston', 2, 3);
insert into stop(id, name, bus_company_id, zone) values(9, 'Ballarat', 2, 4);

insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(1, 1, 1, 2.50);
insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(1, 2, 1, 3.50);
insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(1, 3, 1, 5);
insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(2, 3, 1, 3.75);

insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(1, 1, 1, 2.50);
insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(1, 2, 1, 3.50);
insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(1, 3, 1, 5);
insert into zone_fare(zone_from, zone_to, bus_company_id, price) values(2, 3, 1, 3.75);