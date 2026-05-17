use public;

delete from stop;
delete from tap;

insert into stop(id, name, bus_company_id, zone) values(1, 'Melbourne William St - Bourke St', 1, 1);
insert into stop(id, name, bus_company_id, zone) values(2, 'Melbourne Exhibition St - Bourke St', 1, 1);
insert into stop(id, name, bus_company_id, zone) values(3, 'Southbank', 1, 1);
insert into stop(id, name, bus_company_id, zone) values(4, 'Sunshine', 1, 2);
insert into stop(id, name, bus_company_id, zone) values(5, 'Ballarat', 1, 3);