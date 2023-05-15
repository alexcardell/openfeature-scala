{
  description = "";

  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let pkgs = nixpkgs.legacyPackages.${system}; in
      rec {
        devShell = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdk8
            sbt
          ];
        };
        # packages.x86_64-linux.hello = nixpkgs.legacyPackages.x86_64-linux.hello;
        # defaultPackage.x86_64-linux = self.packages.x86_64-linux.hello;
      });
}
