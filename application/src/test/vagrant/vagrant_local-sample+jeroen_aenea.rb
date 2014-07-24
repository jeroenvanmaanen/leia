# -*- mode: ruby -*-
# vi: set ft=ruby :

module VagrantLocal

  def VagrantLocal.configure(config, ip)
    ## config.vm.share_folder "v-owner-ssh", "/vagrant_owner_ssh", '/Volumes/Keys/ssh'
    config.vm.synced_folder '/Users/jeroen/.m2', "/home/vagrant/.m2"
    require './vagrant_base.rb'
    ## :box => { :name => 'ubuntu-x86_64-12_04_0', :url => 'http://vm.techtribe.nl/boxes/ubuntu-x86_64-12.04.0.box' }
    VagrantBase::configure config, ip, :box => :default
  end

end
