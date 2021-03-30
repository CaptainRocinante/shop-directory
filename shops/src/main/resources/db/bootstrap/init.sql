-- Setup BNPL providers
INSERT INTO bnpl_provider VALUES(
'ed17e284-88fd-46c4-a3e4-38379fbe96e1',
'Afterpay',
'https://www.afterpay.com/',
now(),
now()
);
INSERT INTO bnpl_provider VALUES(
'99ab5896-01d8-4504-86c2-8f82aca0a855',
'Sezzle',
'https://sezzle.com/',
now(),
now()
);
INSERT INTO bnpl_provider VALUES(
'd927a264-efe1-4a16-b664-7e40a0572dc7',
'Klarna',
'https://www.klarna.com/',
now(),
now()
);
INSERT INTO bnpl_provider VALUES(
'cb108c69-b84f-45cf-a0e6-ab0bfa9f016c',
'Affirm',
'https://www.affirm.com/',
now(),
now()
);
INSERT INTO bnpl_provider VALUES(
'f24ec8b3-7f03-4236-9640-43b681bd6fc1',
'Quadpay',
'https://www.quadpay.com/',
now(),
now()
);


-- Setup Afterpay stores
INSERT INTO merchant VALUES(
'c89d4a6f-3c72-4db3-a2a7-77099fb0c531',
'Aritzia',
'https://www.aritzia.com/',
'USA',
true,
'ed17e284-88fd-46c4-a3e4-38379fbe96e1',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'801fd63a-be1d-4656-adb8-6338463c1ecf',
'Ugg',
'https://www.ugg.com/',
'USA',
true,
'ed17e284-88fd-46c4-a3e4-38379fbe96e1',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'70867418-ebd1-4e35-9c6d-7092ded73f63',
'Finish Line',
'https://www.finishline.com/',
'USA',
true,
'ed17e284-88fd-46c4-a3e4-38379fbe96e1',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'dea9163d-aa34-4100-8361-34466dc43b1a',
$$Dillard's$$,
'https://www.dillards.com/',
'USA',
true,
'ed17e284-88fd-46c4-a3e4-38379fbe96e1',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'5fd309bc-9b16-405a-9dca-a5058267a92c',
'Goat',
'https://www.goat.com/',
'USA',
true,
'ed17e284-88fd-46c4-a3e4-38379fbe96e1',
now(),
now(),
NULL
);

-- Setup Klarna stores
INSERT INTO merchant VALUES(
'b476923c-0e8a-4c4b-b4b2-24b6186b7749',
'Shein',
'https://us.shein.com/',
'USA',
true,
'd927a264-efe1-4a16-b664-7e40a0572dc7',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'c94d3513-7345-40b9-8991-d9e660f95482',
'Express',
'https://www.express.com/',
'USA',
true,
'd927a264-efe1-4a16-b664-7e40a0572dc7',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'b823e14c-783f-451a-9cb9-a9666028fcd9',
'Good American',
'https://www.goodamerican.com/',
'USA',
true,
'd927a264-efe1-4a16-b664-7e40a0572dc7',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'cc6db79c-49c6-429f-b17a-aaafe378c1ef',
$$Macy's$$,
'https://www.macys.com/',
'USA',
true,
'd927a264-efe1-4a16-b664-7e40a0572dc7',
now(),
now(),
NULL
);
INSERT INTO merchant VALUES(
'b46e53ea-48aa-4d4c-acb3-36980a6f4062',
'Adidas',
'https://www.adidas.com/',
'USA',
true,
'd927a264-efe1-4a16-b664-7e40a0572dc7',
now(),
now(),
NULL
);