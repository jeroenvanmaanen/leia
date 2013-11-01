# -*- mode: ruby -*-
# vi: set ft=ruby :

module VagrantBase

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

def VagrantBase.configure(config, ip, options = {})
    # how to log:
    #logger = Logger.new(STDOUT)

    defaults = {
        :etc_dir => :default,
        :data_dir => '../data/db-dumps',
      	:box => :default,
    }
    options = defaults.merge(options)
    etc_dir = options[:etc_dir]
    data_dir = options[:data_dir]
    box = options[:box]
    if box == :default
    then
      box = { :name => 'ubuntu-x86_64-12_04_0', :url => 'http://files.vagrantup.com/precise64.box' }
    end

    # All Vagrant configuration is done here. The most common configuration
    # options are documented and commented below. For a complete reference,
    # please see the online documentation at vagrantup.com.

    host_os = RbConfig::CONFIG['host_os']

    # Every Vagrant virtual environment requires a box to build off of.
    config.vm.box = box[:name]
    # config.vbguest.auto_update = true

    # The url from where the 'config.vm.box' box will be fetched if it
    # doesn't already exist on the user's system.
    config.vm.box_url = box[:url]

    # Boot with a GUI so you can see the screen. (Default is headless)
    # config.vm.boot_mode = :gui

    # Assign this VM to a host-only network IP, allowing you to access it
    # via the IP. Host-only networks can talk to the host machine as well as
    # any other machines on the same network, but cannot be accessed (through this
    # network interface) by any external networks.
    # config.vm.network :hostonly, "192.168.33.10"
    config.vm.network :private_network, ip: ip

    # Assign this VM to a bridged network, allowing you to connect directly to a
    # network using the host's network device. This makes the VM appear as another
    # physical device on your network.
    # config.vm.network :bridged

    # Forward a port from the guest to the host, which allows for outside
    # computers to access the VM, whereas host only networking does not.
    # config.vm.forward_port 80, 8080

    # Share an additional folder to the guest VM. The first argument is
    # an identifier, the second is the path on the guest to mount the
    # folder, and the third is the path on the host to the actual folder.
    # config.vm.share_folder "v-data", "/vagrant_data", "../data"
    config.vm.synced_folder "../../..", "/vagrant_project"
    if File.directory?(data_dir)
    then
      config.vm.synced_folder data_dir, "/vagrant_data"
    end
    if etc_dir == :default
    then
      case host_os
        when /linux|darwin/i
          etc_dir = '/etc'
        when /mswin|mingw|cygwin/i
          etc_dir = 'C:/Windows/System32/drivers/etc'
      end
    end
    if etc_dir
    then
      config.vm.synced_folder etc_dir, "/vagrant_host_etc"
    end

    config.vm.provision :shell, :path => "../script/vagrant-provision.sh"
    # Enable provisioning with Puppet stand alone.  Puppet manifests
    # are contained in a directory path relative to this Vagrantfile.
    # You will need to create the manifests directory and a manifest in
    # the file lucid32.pp in the manifests_path directory.
    #
    # An example Puppet manifest to provision the message of the day:
    #
    # # group { "puppet":
    # #   ensure => "present",
    # # }
    # #
    # # File { owner => 0, group => 0, mode => 0644 }
    # #
    # # file { '/etc/motd':
    # #   content => "Welcome to your Vagrant-built virtual machine!
    # #               Managed by Puppet.\n"
    # # }
    #
    # config.vm.provision :puppet do |puppet|
    #   puppet.manifests_path = "manifests"
    #   puppet.manifest_file  = "lucid32.pp"
    # end

    # Enable provisioning with chef solo, specifying a cookbooks path, roles
    # path, and data_bags path (all relative to this Vagrantfile), and adding 
    # some recipes and/or roles.
    #
    # config.vm.provision :chef_solo do |chef|
    #   chef.cookbooks_path = "../my-recipes/cookbooks"
    #   chef.roles_path = "../my-recipes/roles"
    #   chef.data_bags_path = "../my-recipes/data_bags"
    #   chef.add_recipe "mysql"
    #   chef.add_role "web"
    #
    #   # You may also specify custom JSON attributes:
    #   chef.json = { :mysql_password => "foo" }
    # end

    # Enable provisioning with chef server, specifying the chef server URL,
    # and the path to the validation key (relative to this Vagrantfile).
    #
    # The Opscode Platform uses HTTPS. Substitute your organization for
    # ORGNAME in the URL and validation key.
    #
    # If you have your own Chef Server, use the appropriate URL, which may be
    # HTTP instead of HTTPS depending on your configuration. Also change the
    # validation key to validation.pem.
    #
    # config.vm.provision :chef_client do |chef|
    #   chef.chef_server_url = "https://api.opscode.com/organizations/ORGNAME"
    #   chef.validation_key_path = "ORGNAME-validator.pem"
    # end
    #
    # If you're using the Opscode platform, your validator client is
    # ORGNAME-validator, replacing ORGNAME with your organization name.
    #
    # IF you have your own Chef Server, the default validation client name is
    # chef-validator, unless you changed the configuration.
    #
    #   chef.validation_client_name = "ORGNAME-validator"
  end

end
