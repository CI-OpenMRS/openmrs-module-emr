describe("Tests for medical record requests", function() {

    function verifySelectedRecords(numberSelected) {
        for (var i = 0; i < numberSelected; i++) {
            expect(viewModel.recordsToPull()[i].selected()).toBe(true);
        }
        for (var i = numberSelected; i < viewModel.recordsToPull().length; i++) {
            expect(viewModel.recordsToPull()[i].selected()).toBe(false);
        }
    }

    var thirdRecord = RecordRequestModel(3, "Darius", 1, "A033", "Lacoline", "12:34 pm");
    var secondRecord = RecordRequestModel(2, "Mark", 2, "A021", "Lacoline", "12:34 pm");

    var recordsToPull = [
        RecordRequestModel(1, "Alex", 3, "A001", "Mirebalais", "12:34 pm"),
        secondRecord,
        thirdRecord,
        RecordRequestModel(4, "Neil", 4, "A045", "Lacoline", "12:34 pm"),
        RecordRequestModel(5, "Mario", 5, "A101", "Lacoline", "12:34 pm"),
        RecordRequestModel(6, "Renee", 6, "A121", "Lacoline", "12:34 pm"),
        RecordRequestModel(7, "Ellen", 7, "A234", "Lacoline", "12:34 pm"),
        RecordRequestModel(8, "Mike", 8, "A235", "Lacoline", "12:34 pm")
    ];
    var viewModel = RecordRequestsViewModel(recordsToPull);


    it("should select first three records", function() {
        viewModel.selectRequestToBePulled(thirdRecord);
        verifySelectedRecords(3);
    });

    it("should select first two records after selecting three records", function() {
        viewModel.selectRequestToBePulled(thirdRecord);
        viewModel.selectRequestToBePulled(secondRecord);
        verifySelectedRecords(2);
    });

    it("should select by number", function() {
        viewModel.selectNumber(4);
        verifySelectedRecords(4);
        viewModel.selectNumber(1);
        verifySelectedRecords(1);
    });

    it("should select all if number is larger than size", function () {
       viewModel.selectNumber(10);
        verifySelectedRecords(8);
    });

    it("should compute when none are selected", function() {
        viewModel.selectNumber(0);
        expect(viewModel.selectedRequests().length).toBe(0);
    });

    it("should compute when some are selected", function() {
        viewModel.selectNumber(2);
        expect(viewModel.selectedRequests().length).toBe(2);
    });
})