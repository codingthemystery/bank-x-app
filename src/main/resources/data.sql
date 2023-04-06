INSERT INTO `banks` (`id`, `address`, `email`, `name`, `phone_number`, `status`, `swift_code`) VALUES
	(1, 'Johannesburg, Gauteng', 'info@bankx.co.za', 'BankX', '+27110000000', 0, 'BANKXZAJJ'),
	(2, 'Johannesburg, Gauteng', 'info@bankz.co.za', 'BankZ', '+27111111111', 0, 'BANKZZAJJ');


INSERT INTO `customers` (`id`, `address`, `email`, `first_name`, `last_name`, `phone_number`, `status`, `token`, `bank_id`) VALUES
	(1, 'Edenvale, Gauteng', 'sonian@nowhere111.co.za', 'Sonia', 'Nel', '+27824000000', 0, '', 1),
	(2, 'Knysna, Western Cape', 'robertg@nowhere111.co.za', 'Robert', 'Gerome', '+27796666666', 0, '', 1),
	(3, 'Humansdorp, Eastern Cape', 'robertg@nowhere111.co.za', 'Martha', 'de Jongh', '+27796666666', 0, '', 1),
	(4, 'Cape Town, Wetern Cape', 'tinS@nowhere111.co.za', 'Tim', 'Smit', '+27796666666', 0, '', 2);
	

INSERT INTO `accounts` (`id`, `balance`, `card_number`, `status`, `type`, `customer_id`) VALUES
	(1, 500.00, 4284827130925186, 0, 0, 1),
	(2, 0.00, 8898299367825558, 0, 1, 1),
	(3, 500.00, 6704427109697995, 0, 0, 2),
	(4, 0.00, 5382298335942481, 0, 1, 2),
	(5, 500.00, 7783031078924439, 0, 0, 3),
	(6, 0.00, 4545798498230057, 0, 1, 3),
	(7, 500.00, 5113344337257814, 0, 0, 4),
	(8, 0.00, 2409193247361583, 0, 1, 4);

INSERT INTO `transactions` (`id`, `amount`, `date`, `destination_card_number`, `error`, `external_reference`, `originating_card_number`, `processing_type`, `reference`, `status`, `transaction_group`, `transaction_type`, `customer_bank_id`, `destination_account_id`, `originating_account_id`, `processing_bank_id`) VALUES
	(1, 500.00, '2023-04-03 02:05:16', 4284827130925186, '', NULL, NULL, 0, 'Joining Bonus', 2, 3263480453647830, 0, 1, 1, NULL, 1),
	(2, 500.00, '2023-04-03 02:05:16', 6704427109697995, '', NULL, NULL, 0, 'Joining Bonus', 2, 4305678396107900, 0, 1, 3, NULL, 1),
	(3, 500.00, '2023-04-03 02:05:16', 7783031078924439, '', NULL, NULL, 0, 'Joining Bonus', 2, 6261590353780329, 0, 1, 5, NULL, 1),
	(4, 500.00, '2023-04-03 02:05:16', 5113344337257814, '', NULL, NULL, 0, 'Joining Bonus', 2, 1614552916086061, 0, 2, 7, NULL, 1);