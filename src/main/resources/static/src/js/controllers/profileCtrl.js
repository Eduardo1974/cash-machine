/**
 * Created by Eduardo Alves.
 * Email: <eduardodinizo@hotmail.com>
 */

(function () {
    'use strict';

    angular.module('app')
        .controller('profileCtrl', ['$scope', '$timeout', '$interval', 'toastr', 'SweetAlert', 'profileService', 'authUser', 'loginService', '$location',
                function ($scope, $timeout, $interval, toastr, SweetAlert, profileService, authUser, loginService, $location) {

                    var KEY_STORAGE = 'token';
                    var previous = StorageHelper.getItem("previous_page");
                    var user = authUser.getUser();

                    $scope.isEdit = 'salvar';
                    $scope.account = {};
                    $scope.user = {
                        authorities: "ROLE_USER",
                        account: $scope.account
                    };


                    if (previous !== "login") {
                        authUser.authorize();
                        $scope.isEdit = 'editar';
                    }
                    StorageHelper.setItem("previous_page", "profile");

                    $scope.saveProfile = function () {
                        if ($scope.form.$valid) {
                            profileService.saveProfile($scope.user)
                                .then(function (res) {
                                    if (res.status === 201) {
                                        $scope.user = res.data;
                                        refreshToken(res.data);
                                        toastr.success('Salvo com sucesso', {timeOut: 900});
                                    } else if (res.status === 200) {
                                        refreshToken(res.data);
                                        toastr.success('Editado com sucesso', {timeOut: 900});
                                    } else {
                                        toastr.error('Não foi possível salvar o usuário', {timeOut: 900});
                                    }
                                })
                                .catch(function (res) {
                                    if (res.status === 409) {
                                        toastr.error('Usuário Existente!', {timeOut: 900});
                                    }
                                })
                        }
                    };

                    $scope.deleteProfile = function () {
                        SweetAlert.swal({
                            title: 'Remover?',
                            text: 'Este registro será removido permanentemente.',
                            type: 'warning',
                            showCancelButton: true,
                            confirmButtonColor: '#DD6B55',
                            confirmButtonText: 'Sim',
                            cancelButtonText: 'Cancelar',
                            closeOnConfirm: true
                        }, function (isConfirm) {
                            if (isConfirm) {
                                $scope.user.username = user.username;
                                profileService.deleteProfile($scope.user)
                                    .then(function (res) {
                                        if (res.status === 200) {
                                            logout();
                                            toastr.success('Deletado com Sucesso', {timeOut: 900});
                                        }  else {
                                            toastr.error('Não foi possível deletar o usuário', {timeOut: 900});
                                        }
                                    })
                                    .catch(function (res) {
                                        if (res.status === 404) {
                                            toastr.error('Usuário não encontrado', {timeOut: 900});
                                        }
                                    });
                            }
                        });
                    };

                    function loadProfile() {
                        if(typeof user !== "undefined") {
                            profileService.loadProfile(user)
                                .then(function (user) {
                                    if (user.status === 200) {
                                        $scope.user = user.data;
                                        $scope.user.password = "";
                                        $scope.account = $scope.user.account;
                                        toastr.success('Carregado com Sucesso', {timeOut: 900});
                                    }
                                })
                                .catch(function (user) {
                                    if (user.status === 404) {
                                        toastr.error('Usuário não encontrado', {timeOut: 900});
                                    }
                                });
                        }
                    }
                    loadProfile();

                    function refreshToken(entry) {
                        loginService.refreshToken(entry)
                            .then(function (data) {
                                if (status === 200) {
                                    toastr.success('Carregado com Sucesso', {timeOut: 900});
                                } else if (data.status === 404) {
                                    toastr.error('Usuário não encontrado', {timeOut: 900});
                                }
                                StorageHelper.setItem(KEY_STORAGE, data.token);
                                authUser.setLogged(true);
                                authUser.setUser(entry);
                                user = authUser.getUser();
                            })
                            .catch(function (error) {
                                console.log(error);
                            });
                    }

                    $scope.back = function () {
                        $location.path('/menu');
                    };

                    function logout() {
                        StorageHelper.removeItem(KEY_STORAGE);
                        authUser.setLogged(false);
                        authUser.removeCookies();
                        user = "";
                        $location.path('/');
                    }
                }
            ]
        );
}());