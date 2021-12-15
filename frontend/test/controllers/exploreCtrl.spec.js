describe('ExploreCtrl', function() {
  beforeEach(module('frontend'));

  var $controller;

  beforeEach(inject(function(_$controller_){
    // The injector unwraps the underscores (_) from around the parameter names when matching
    $controller = _$controller_;
  }));

  describe('$scope.activeTab', function() {
    var $scope, controller;

    beforeEach(function() {
      $scope = {};
      controller = $controller('ExploreCtrl', { $scope: $scope });
    });


    it('sets active tab to tech when post tab is selected and tech tab is pressed ', function() {
      $scope.activeTab = 'P';
      $scope.setActiveTab('T');
      expect($scope.activeTab).toEqual('T');
    });
  });
});
